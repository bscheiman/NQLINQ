package org.nqlinq.ant;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbField {
    protected static Pattern FieldRegex = Pattern.compile("^([^ ]+)\\s*(\\w+(?:\\([^\\)]+\\))?)\\s*(.+)$", Pattern.CASE_INSENSITIVE);
    protected static Pattern FieldSize = Pattern.compile("\\((\\d+)(?:, \\d+)?\\)", Pattern.CASE_INSENSITIVE);

    private String Field;
    private String Type;
    private int Length;

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public DbField(String fieldSql) {
        Matcher m = FieldRegex.matcher(fieldSql);

        if (m.matches()) {
            Field = m.group(1);
            Type = m.group(2);
            Length = 0;

            System.out.println(MessageFormat.format(" - Field: {0}", Field));

            Matcher fieldSize = FieldSize.matcher(Type);

            if (fieldSize.find()) {
                Length = Integer.parseInt(fieldSize.group(1));
                Type = fieldSize.replaceAll("");
            }

            System.out.println(MessageFormat.format("  - Type: {0} -> {1}", Type, getMappedType()));
            System.out.println(MessageFormat.format("  - Size: {0}", Length));
        } else {
            System.out.println(MessageFormat.format("{0} didn''t match.", fieldSql));
        }
    }

    public String getMappedType() {
        if (getType().equals("VARCHAR2") || getType().equals("CHAR") || getType().equals("VARCHAR"))
            return "String";

        if (getType().equals("NUMBER") && getLength() == 1)
            return "boolean";

        if (getType().equals("NUMBER") || getType().equals("INT") || getType().equals("TINYINT") || getType().equals("SMALLINT"))
            return "int";

        if (getType().equals("TIMESTAMP"))
            return "Timestamp";

        if (getType().equals("FLOAT"))
            return "float";

        if (getType().equals("DECIMAL"))
            return "double";

        if (getType().equals("NUMERIC") || getType().equals("BIGINT"))
            return "BigDecimal";

        if (getType().equals("DECIMAL"))
            return "double";

        if (getType().equals("DECIMAL"))
            return "double";

        return Type;
    }
}
