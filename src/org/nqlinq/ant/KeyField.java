package org.nqlinq.ant;

import org.nqlinq.helpers.WordHelper;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyField {
    protected static Pattern KeyRegex = Pattern.compile("FOREIGN KEY \\(([^\\)]+)\\) REFERENCES ([^\\(]+)\\(([^\\)]+)\\)", Pattern.CASE_INSENSITIVE);

    protected String IdField;
    protected String Table;
    protected String TableObject;

    public String getTableObject() {
        return TableObject;
    }

    public void setTableObject(String tableObject) {
        TableObject = tableObject;
    }

    public String getIdField() {
        return IdField;
    }

    public void setIdField(String idField) {
        IdField = idField;
    }

    public String getTable() {
        return Table;
    }

    public void setTable(String table) {
        Table = table;
    }

    public KeyField(String fieldSql) {
        Matcher m = KeyRegex.matcher(fieldSql);

        if (m.matches()) {
            IdField = m.group(1);
            Table = m.group(2);
            TableObject = WordHelper.singularize(Table);

            System.out.println(MessageFormat.format(" - Foreign Key: {0}", IdField));
            System.out.println(MessageFormat.format("  - Table: {0} ({1})", Table, TableObject));
        } else {
            System.out.println(MessageFormat.format("{0} didn''t match.", fieldSql));
        }
    }
}
