package org.nqlinq.core;

import com.asn1c.core.Int32;
import org.nqlinq.annotations.Column;
import org.nqlinq.annotations.Sequence;
import org.nqlinq.annotations.Table;
import org.nqlinq.helpers.StringHelper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({ "UnusedDeclaration" })
public class Entity<T> {
    protected int id;
    protected boolean isDirty;
    protected static HashMap<String, HashMap<String, Method>> ReflectionInfo = new HashMap<String, HashMap<String, Method>>();
    protected static HashMap<String, String> UpdateQueries = new HashMap<String, String>();
    protected static HashMap<String, String> InsertQueries = new HashMap<String, String>();
    protected static HashMap<String, String> DeleteQueries = new HashMap<String, String>();
    protected UnitOfWork unitOfWork;

    public void Parse(UnitOfWork uow, ResultSet rs) {
        String className = this.getClass().getName();
        generateQueries();
        unitOfWork = uow;

        try {
            if (!ReflectionInfo.containsKey(className))
                ReflectionInfo.put(className, new HashMap<String, Method>());

            if (ReflectionInfo.get(className).isEmpty()) {
                Method[] methods = this.getClass().getMethods();

                for (Method method : methods) {
                    if (!method.getName().startsWith("set"))
                        continue;

                    Column col = method.getAnnotation(Column.class);

                    ReflectionInfo.get(className).put(col.name(), method);
                }

                Method method = this.getClass().getSuperclass().getDeclaredMethod("setId", String.class);
                method.setAccessible(true);

                Column col = method.getAnnotation(Column.class);

                ReflectionInfo.get(className).put(col.name(), method);
            }

            try {
                ResultSetMetaData metaData = rs.getMetaData();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String column = metaData.getColumnName(i).toUpperCase();

                    ReflectionInfo.get(className).get(column).invoke(this, rs.getString(i));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            isDirty = false;
        } catch (Exception ex) {
        }
    }

    public Entity() {
        setId("-1");
        isDirty = true;
    }

    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void delete(UnitOfWork uow) {
        String className = this.getClass().getName();

        if (id < 0)
            return;

        Table table = this.getClass().getAnnotation(Table.class);

        uow.ExecuteSql(DeleteQueries.get(className), new Object[] { id });
    }

    public void generateQueries() {
        String className = this.getClass().getName();

        if (InsertQueries.containsKey(className) || UpdateQueries.containsKey(className) || DeleteQueries.containsKey(className))
            return;

        ArrayList<String> columns = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> updates = new ArrayList<String>();
        Method[] methods = this.getClass().getMethods();
        Sequence seq = this.getClass().getAnnotation(Sequence.class);
        Table table = this.getClass().getAnnotation(Table.class);

        columns.add("ID");
        values.add(MessageFormat.format("{0}.nextval", seq.name()));

        for (Method method : methods) {
            if (!method.getName().startsWith("get") || method.getName().endsWith("Id"))
                continue;

            Column col = method.getAnnotation(Column.class);

            if (col == null)
                continue;

            columns.add(col.name());
            values.add("?");
            updates.add(MessageFormat.format("{0} = ?", col.name()));
        }

        InsertQueries.put(className, MessageFormat.format("INSERT INTO {0}({1}) VALUES({2})", table.name(), StringHelper.join(columns), StringHelper.join(values)));
        UpdateQueries.put(className, MessageFormat.format("UPDATE {0} SET {1} WHERE ID = ?", table.name(), StringHelper.join(updates)));
        DeleteQueries.put(className, MessageFormat.format("DELETE FROM {0} WHERE ID = ''?''", table.name()));
    }

    public void save(UnitOfWork uow) {
        String className = this.getClass().getName();

        if (!isDirty)
            return;

        try {
            HashMap<String, String> keyValue = new HashMap<String, String>();

            Method[] methods = this.getClass().getMethods();

            for (Method method : methods) {
                if (!method.getName().startsWith("get") || method.getName().endsWith("Id"))
                    continue;

                Column col = method.getAnnotation(Column.class);

                if (col == null)
                    continue;

                keyValue.put(col.name(), MessageFormat.format("{0}", method.invoke(this)));
            }

            if (id < 0) {
                Sequence seq = this.getClass().getAnnotation(Sequence.class);
                Object[] objects = new Object[keyValue.keySet().size()];
                int currObj = 0;

                for (String val : keyValue.values())
                    objects[currObj++] = val;

                setId(uow.ExecuteInsert(InsertQueries.get(className), seq.name(), objects));
            } else {
                Object[] objects = new Object[keyValue.keySet().size() + 1];
                objects[objects.length - 1] = id;
                int currObj = 0;

                for (String val : keyValue.values())
                    objects[currObj++] = val;

                uow.ExecuteSql(UpdateQueries.get(className), objects);
            }

            isDirty = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Column(name = "ID")
    protected Entity setId(String value) {
        id = Int32.parseInt(value);

        return this;
    }
}
