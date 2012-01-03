package org.nqlinq.ant;

import org.nqlinq.helpers.WordHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Holder {
    public final String UnitOfWork;
    public final HashMap<String, String[]> TableContents;
    public final HashMap<String, ArrayList<DbField>> TableFields;
    public final HashMap<String, ArrayList<KeyField>> TableKeyFields;
    public final HashMap<String, ArrayList<String>> IncomingKeyFields;
    public final String Source;
    public final String Package;
    public final String Sequence;
    public final String Driver;
    public final String Url;
    public final String User;
    public final String Password;

    public Holder(String unitOfWork, String driver, String url, String user, String password, String source, String pkg, String seq) {
        UnitOfWork = unitOfWork;
        Driver = driver;
        Url = url;
        User = user;
        Password = password;
        Source = source;
        Package = pkg;
        Sequence = seq;

        TableContents = new HashMap<String, String[]>();
        TableFields = new HashMap<String, ArrayList<DbField>>();
        TableKeyFields = new HashMap<String, ArrayList<KeyField>>();
        IncomingKeyFields = new HashMap<String, ArrayList<String>>();
    }

    public void add(String table, String[] contents) {
        TableContents.put(table, contents);
    }

    public void addField(String table, DbField field) {
        if (TableFields.get(table) == null)
            TableFields.put(table, new ArrayList<DbField>());

        boolean found = false;

        for (DbField dbField : TableFields.get(table)) {
            if (dbField.getField().equals(field.getField()))
                found = true;
        }

        if (!found)
            TableFields.get(table).add(field);
    }

    public void addKeyField(String table, KeyField field) {
        if (TableKeyFields.get(table) == null)
            TableKeyFields.put(table, new ArrayList<KeyField>());

        boolean found = false;

        for (KeyField keyField : TableKeyFields.get(table)) {
            if (keyField.getIdField().equals(field.getIdField()))
                found = true;
        }

        if (!found)
            TableKeyFields.get(table).add(field);
    }

    public void addExternalKeyField(String table, String externalTable) {
        if (IncomingKeyFields.get(table) == null)
            IncomingKeyFields.put(table, new ArrayList<String>());

        IncomingKeyFields.get(table).add(externalTable);
    }

    public void execute() {
        PrintStream UnitOfWorkStream = GetStream(GetPath(UnitOfWork));

        UnitOfWorkStream.println(MessageFormat.format("package {0};", Package));
        UnitOfWorkStream.println();
        UnitOfWorkStream.println("import org.nqlinq.annotations.*;");
        UnitOfWorkStream.println("import org.nqlinq.commands.*;");
        UnitOfWorkStream.println("import org.nqlinq.constraints.*;");
        UnitOfWorkStream.println("import org.nqlinq.core.*;");
        UnitOfWorkStream.println("import org.nqlinq.helpers.*;");
        UnitOfWorkStream.println("import org.nqlinq.objects.*;");
        UnitOfWorkStream.println("import org.nqlinq.queries.*;");
        UnitOfWorkStream.println("import org.nqlinq.exceptions.*;");
        UnitOfWorkStream.println();
        UnitOfWorkStream.println("@SuppressWarnings(\"ALL\")");
        UnitOfWorkStream.println(MessageFormat.format("@JdbcConnection(driver = \"{0}\", url = \"{1}\", user = \"{2}\", password = \"{3}\")", Driver, Url, User, Password));
        UnitOfWorkStream.println(MessageFormat.format("public class {0} extends UnitOfWork '{'", UnitOfWork));
        UnitOfWorkStream.println(MessageFormat.format("    public {0}() '{'", UnitOfWork));
        UnitOfWorkStream.println("        super();");
        UnitOfWorkStream.println("    }");
        UnitOfWorkStream.println();

        try {
            for (String table : TableContents.keySet()) {
                String singular = WordHelper.singularize(table);
                String plural = WordHelper.pluralize(table);
                PrintStream whereStream = GetStream(GetPath(MessageFormat.format("where{0}Where", File.separator), singular));

                UnitOfWorkStream.println(MessageFormat.format("    @TableObject(name = \"{0}.{1}\")", Package, singular));
                UnitOfWorkStream.println("    @SuppressWarnings(\"unchecked\")");
                UnitOfWorkStream.println(MessageFormat.format("    public Queryable<{0}> {1}(String str) '{'", singular, plural));
                UnitOfWorkStream.println(MessageFormat.format("        TableAnnotationHolder holder = AnnotationHelper.GetTableAnnotations(this.getClass(), \"{0}\");", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("        Queryable<{0}> q = new Queryable<{0}>(this, holder.getTableObject().name(), new SelectCommand(holder.getTable().name(), new Where(str)));", singular, plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("        for ({0} obj: q)", singular));
                UnitOfWorkStream.println("            add(obj);");
                UnitOfWorkStream.println();
                UnitOfWorkStream.println("        return q;");
                UnitOfWorkStream.println("    }");
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public Queryable<{0}> {1}() '{' return {1}(\"1 = 1 ORDER BY ID\"); '}'", singular, plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public Queryable<{0}> {1}(OperationBuilder oper) throws InvalidOperationException '{' return {1}(oper.getOperation()); '}'", singular, plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public Queryable<{0}> {1}(DualOperation oper) '{' return {1}(oper.toString()); '}'", singular, plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public Queryable<{0}> {1}(Operation oper) '{' return {1}(oper.toString()); '}'", singular, plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    @TableObject(name = \"{0}.{1}\")", Package, singular));
                UnitOfWorkStream.println("    @SuppressWarnings(\"unchecked\")");
                UnitOfWorkStream.println(MessageFormat.format("    public {0} Single{0}(int id) throws InvalidOperationException '{'", singular));
                UnitOfWorkStream.println(MessageFormat.format("        TableAnnotationHolder holder = AnnotationHelper.GetTableAnnotations(this.getClass(), \"Single{0}\", Integer.TYPE);", singular));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("        Single<{0}> single = new Single<{0}>(this, holder.getTable().name(), holder.getTableObject().name(), id);", singular));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println("        add(single.getObject());");
                UnitOfWorkStream.println();
                UnitOfWorkStream.println("        return single.getObject();");
                UnitOfWorkStream.println("    }");
                UnitOfWorkStream.println();
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    @TableObject(name = \"{0}.{1}\")", Package, singular));
                UnitOfWorkStream.println("    @SuppressWarnings(\"unchecked\")");
                UnitOfWorkStream.println(MessageFormat.format("    public int Count{0}(String str) '{'", plural));
                UnitOfWorkStream.println(MessageFormat.format("        TableAnnotationHolder holder = AnnotationHelper.GetTableAnnotations(this.getClass(), \"Count{0}\");", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println("        return new Count(this, new CountCommand(holder.getTable().name(), new Where(str))).getValue();");
                UnitOfWorkStream.println("    }");
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public int Count{0}() '{' return Count{0}(\"1 = 1\"); '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public int Count{0}(OperationBuilder oper) throws InvalidOperationException '{' return Count{0}(oper.getOperation()); '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public int Count{0}(Operation oper) '{' return Count{0}(oper.toString()); '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public int Count{0}(DualOperation oper) '{' return Count{0}(oper.toString()); '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public boolean Any{0}() '{' return Count{0}(\"1 = 1\") > 0; '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public boolean Any{0}(String str) '{' return Count{0}(str) > 0; '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public boolean Any{0}(OperationBuilder oper) throws InvalidOperationException '{' return Count{0}(oper.getOperation()) > 0; '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public boolean Any{0}(Operation oper) '{' return Count{0}(oper.toString()) > 0; '}'", plural));
                UnitOfWorkStream.println();
                UnitOfWorkStream.println(MessageFormat.format("    public boolean Any{0}(DualOperation oper) '{' return Count{0}(oper.toString()) > 0; '}'", plural));
                UnitOfWorkStream.println();
                PrintStream objStream = GetStream(GetPath(singular));

                objStream.println(MessageFormat.format("package {0};", Package));
                objStream.println();
                objStream.println("import org.nqlinq.annotations.*;");
                objStream.println("import org.nqlinq.core.*;");
                objStream.println("import org.nqlinq.exceptions.*;");
                objStream.println(MessageFormat.format("import {0}.where.*;", Package));
                objStream.println("import org.nqlinq.helpers.ConversionHelper;");
                objStream.println("import java.sql.Timestamp;");
                objStream.println("import java.math.BigDecimal;");
                objStream.println();
                objStream.println("@SuppressWarnings(\"ALL\")");
                objStream.println(MessageFormat.format("@Sequence(name = \"{0}\")", Sequence));
                objStream.println(MessageFormat.format("@Table(name = \"{0}\")", table));
                objStream.println(MessageFormat.format("public class {0} extends Entity<{0}> '{'", singular));

                whereStream.println(MessageFormat.format("package {0}.where;", Package));
                whereStream.println();
                whereStream.println(MessageFormat.format("public class Where{0} '{'", singular));

                for (DbField field : TableFields.get(table)) {
                    whereStream.println(MessageFormat.format("    public static {0}.where.{1}.{2} {2};", Package, singular.toLowerCase(), field.getField()));

                    if (!field.getField().toUpperCase().equals("ID")) {
                        objStream.println(MessageFormat.format("    protected {0} {1};", field.getMappedType(), field.getField().toLowerCase()));
                        objStream.println();
                        objStream.println(MessageFormat.format("    @Column(name = \"{0}\")", field.getField().toUpperCase()));
                        objStream.println(MessageFormat.format("    public {0} get{1}() '{' return {2}; '}'", field.getMappedType(), field.getField(), field.getField().toLowerCase()));
                        objStream.println();
                        objStream.println(MessageFormat.format("    @Column(name = \"{0}\")", field.getField().toUpperCase()));
                        objStream.println(MessageFormat.format("    public {0} set{1}(String newVal) '{'", singular, field.getField()));
                        objStream.println(MessageFormat.format("        {0} = ConversionHelper.ConvertTo{1}(newVal);", field.getField().toLowerCase(), title(field.getMappedType())));
                        objStream.println("        isDirty = true;");
                        objStream.println();
                        objStream.println("        return this;");
                        objStream.println("    }");
                        objStream.println();

                        if (!field.getMappedType().equals("String")) {
                            objStream.println(MessageFormat.format("    @Column(name = \"{0}\")", field.getField().toUpperCase()));
                            objStream.println(MessageFormat.format("    public {0} set{1}({2} newVal) '{'", singular, field.getField(), field.getMappedType()));
                            objStream.println(MessageFormat.format("        {0} = newVal;", field.getField().toLowerCase()));
                            objStream.println("        isDirty = true;");
                            objStream.println();
                            objStream.println("        return this;");
                            objStream.println("    }");
                            objStream.println();
                        }
                    }

                    PrintStream whereFieldStream = GetStream(GetPath(MessageFormat.format("where{0}{1}{0}", File.separator, singular.toLowerCase()), field.getField()));
                    String obj = "obj";

                    if (field.getMappedType().equals("int"))
                        obj = "Long.toString(obj)";

                    whereFieldStream.println(MessageFormat.format("package {0}.where.{1};", Package, singular.toLowerCase()));
                    whereFieldStream.println();
                    whereFieldStream.println("import java.text.MessageFormat;");
                    whereFieldStream.println("import java.sql.Timestamp;");
                    whereFieldStream.println("import org.nqlinq.queries.Operation;");
                    whereFieldStream.println("import java.math.BigDecimal;");
                    whereFieldStream.println();
                    whereFieldStream.println("@SuppressWarnings(\"ALL\")");
                    whereFieldStream.println(MessageFormat.format("public class {0} '{'", field.getField()));
                    whereFieldStream.println(MessageFormat.format("    public static Operation Equals({0} obj) '{' return new Operation(\"{1}\", \"=\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation DoesNotEquals({0} obj) '{' return new Operation(\"{1}\", \"!=\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation IsGreaterThan({0} obj) '{' return new Operation(\"{1}\", \">\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation IsLessThan({0} obj) '{' return new Operation(\"{1}\", \"<\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation IsGreaterThanOrEquals({0} obj) '{' return new Operation(\"{1}\", \">=\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation IsLessThanOrEquals({0} obj) '{' return new Operation(\"{1}\", \"<=\", {2}); '}'", field.getMappedType(), field.getField().toUpperCase(), obj));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation Like(String str) '{' return new Operation(\"{1}\", \"LIKE\", str); '}'", field.getMappedType(), field.getField().toUpperCase()));
                    whereFieldStream.println();
                    whereFieldStream.println(MessageFormat.format("    public static Operation In(String str) '{' return new Operation(\"{1}\", \"IN\", MessageFormat.format(\"('{'0'}')\", str)); '}'", field.getMappedType(), field.getField().toUpperCase()));
                    whereFieldStream.println("}");

                    whereFieldStream.close();
                }

                if (TableKeyFields.get(table) != null) {
                    for (KeyField field : TableKeyFields.get(table))
                        objStream.println(MessageFormat.format("    public {0} get{0}() '{' try '{' return (({1}) unitOfWork).Single{0}({2}); '}' catch (InvalidOperationException e) '{' return null; '}' '}'", field.getTableObject(), UnitOfWork, field.getIdField().toLowerCase()));
                }

                if (IncomingKeyFields.get(table) != null) {
                    for (String field : IncomingKeyFields.get(table)) {
                        String singularField = WordHelper.singularize(field);
                        String pluralField = WordHelper.pluralize(field);

                        objStream.println(MessageFormat.format("    public Queryable<{0}> get{1}() '{' return (({2}) unitOfWork).{1}(Where{0}.{3}Id.Equals(id)); '}'", singularField, pluralField, UnitOfWork, singular));
                    }
                }

                objStream.println();
                objStream.println(MessageFormat.format("    public {0}() '{' '}'", singular));
                objStream.println();
                objStream.println(MessageFormat.format("    public {0}({0} obj) '{'", singular));

                for (DbField field : TableFields.get(table)) {
                    objStream.println(MessageFormat.format("        {0} = obj.{0};", field.getField().toLowerCase()));
                }
                objStream.println("    }");

                objStream.println("}");
                whereStream.println("}");
                objStream.close();
                whereStream.close();
            }
        } finally {
            UnitOfWorkStream.println("}");
            UnitOfWorkStream.close();
        }
    }

    public static String title(String string) {
        String result = "";

        for (int i = 0; i < string.length(); i++) {
            String next = string.substring(i, i + 1);

            if (i == 0) {
                result += next.toUpperCase();
            } else {
                result += next.toLowerCase();
            }
        }

        return result;
    }

    private String GetPath(String suffix, String file) {
        if (!file.endsWith(".java"))
            file += ".java";

        StringBuilder sb = new StringBuilder();

        sb.append(Source);
        sb.append(File.separator);
        sb.append(Package.replace(".", File.separator));
        sb.append(File.separator);
        sb.append(suffix);
        sb.append(file);

        return sb.toString();
    }

    private String GetPath(String file) {
        return GetPath("", file);
    }

    private PrintStream GetStream(String file) {
        try {
            File fileObj = new File(file);

            File dirs = new File(fileObj.getPath().substring(0, fileObj.getPath().lastIndexOf(File.separator)));
            dirs.mkdirs();

            return new PrintStream(new FileOutputStream(file));
        } catch (Exception ex) {
        }

        return null;
    }
}
