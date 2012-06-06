package org.nqlinq.core;

import org.nqlinq.annotations.Column;
import org.nqlinq.annotations.Sequence;
import org.nqlinq.annotations.Table;
import org.nqlinq.constants.DbStrings;
import org.nqlinq.helpers.StringHelper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({ "UnusedDeclaration" })
public class Entity<T> {
    protected long Id;
    protected boolean isDirty;
    protected static HashMap<String, HashMap<String, Method>> ReflectionInfo = new HashMap<String, HashMap<String, Method>>();
    protected static HashMap<String, String> UpdateQueries = new HashMap<String, String>();
    protected static HashMap<String, String> InsertQueries = new HashMap<String, String>();
    protected static HashMap<String, String> DeleteQueries = new HashMap<String, String>();
    protected UnitOfWork unitOfWork;

    public void Parse(UnitOfWork uow, ResultSet rs) {
        unitOfWork = uow;
        String className = this.getClass().getName();

        try {
            if (!ReflectionInfo.containsKey(className))
                ReflectionInfo.put(className, new HashMap<String, Method>());

            if (ReflectionInfo.get(className).isEmpty()) {
                Method[] methods = this.getClass().getMethods();

                for (Method method : methods) {
                    if (!method.getName().startsWith("set") || !method.getParameterTypes()[0].getName().endsWith("String"))
                        continue;

                    Column col = method.getAnnotation(Column.class);

                    if(col != null)
                        ReflectionInfo.get(className).put(col.name(), method);
                }
                Class entityClass = this.getClass().getSuperclass().getSuperclass();
                Method method = entityClass.getDeclaredMethod("setId", java.lang.String.class);
                method.setAccessible(true);

                Column col = method.getAnnotation(Column.class);

                if(col != null)
                    ReflectionInfo.get(className).put(col.name(), method);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
    }

    public Entity() {
        setId("-1");
        isDirty = true;
    }

    @Column(name = "ID")
    public long getId() {
        return Id;
    }

    public void delete(UnitOfWork uow) {
        unitOfWork = uow;
        generateQueries();
        String className = this.getClass().getName();

        if (Id < 0)
            return;

        Table table = this.getClass().getAnnotation(Table.class);

        unitOfWork.ExecuteSql(DeleteQueries.get(className), new Object[] { Id });
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

        if (StringHelper.isNullOrEmpty(unitOfWork.dbms) || unitOfWork.dbms.equals("Oracle")) {
            columns.add("ID");
            values.add(MessageFormat.format(DbStrings.IdentityNextVal, seq.name()));
        } else if(unitOfWork.dbms.equals("Postgres")){
            columns.add("ID");
            values.add(MessageFormat.format(DbStrings.IdentityNextVal, seq.name()));
        }

        for (Method method : methods) {
            if (!method.getName().startsWith("get") || method.getName().equals("getId"))
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
        DeleteQueries.put(className, MessageFormat.format("DELETE FROM {0} WHERE ID = ?", table.name()));
    }

    @SuppressWarnings("unchecked")
    public void save(UnitOfWork uow) {
        unitOfWork = uow;
        generateQueries();
        String className = this.getClass().getName();

        if (!isDirty)
            return;

        try {
            ArrayList values = new ArrayList<String>();
            Method[] methods = this.getClass().getMethods();
            for (Method method : methods) {
                if (!method.getName().startsWith("get") || method.getName().equals("getId"))
                    continue;

                Column col = method.getAnnotation(Column.class);

                if (col == null)
                    continue;
                if(method.getName().endsWith("Id") && method.invoke(this).toString().equals("0")) {
                    values.add(null);
                }
                else if(method.getName().endsWith("Id") || method.getReturnType().getName().toLowerCase().endsWith("long")
                        || method.getReturnType().getName().toLowerCase().endsWith("int")
                        || method.getReturnType().getName().toLowerCase().endsWith("float")
                        || method.getReturnType().getName().toLowerCase().endsWith("double"))
                    values.add(MessageFormat.format("{0}", method.invoke(this)).replaceAll(",", ""));
                else if(method.getReturnType().getName().toLowerCase().endsWith("boolean"))
                    values.add(Boolean.parseBoolean(method.invoke(this).toString()) ? 1 : 0);
                else
                    values.add(MessageFormat.format("{0}", method.invoke(this)));
            }

            if (Id < 0) {
                Sequence seq = this.getClass().getAnnotation(Sequence.class);
                setId(unitOfWork.ExecuteInsert(InsertQueries.get(className), seq.name(), values.toArray()));
            } else {
                values.add(MessageFormat.format("{0}", Id).replaceAll(",", ""));
                unitOfWork.ExecuteSql(UpdateQueries.get(className), values.toArray());
            }

            isDirty = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Column(name = "ID")
    protected Entity setId(String value) {
        Id = Long.parseLong(value);

        return this;
    }
}
