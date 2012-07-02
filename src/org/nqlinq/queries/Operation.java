package org.nqlinq.queries;

import org.nqlinq.helpers.StringHelper;

import java.text.MessageFormat;

public class Operation {
    public static final String EQUALS = "=";
    public static final String NOTEQUALS = "!=";
    public static final String GREATER = ">";
    public static final String LESSER = "<";
    public static final String GREATEREQUAL = ">=";
    public static final String LESSEREQUAL = "<=";
    public static final String IN = "IN";

    private final String Column;
    private final String Operator;
    private final Object Target;
    private String Order;

    public Operation(String column, String operator, Object target) {
        this.Column = column;
        this.Operator = operator;
        this.Target = target;
    }

    public DualOperation and(Operation target) {
        return new DualOperation(this, "AND", target);
    }

    public DualOperation or(Operation target) {
        return new DualOperation(this, "OR", target);
    }
    
    public DualOperation and(DualOperation target) {
        return new DualOperation(this, "AND", target);
    }

    public DualOperation or(DualOperation target) {
        return new DualOperation(this, "OR", target);
    }

    public Operation orderBy(String order) {
        this.Order = order;

        return this;
    }

    @Override
    public String toString() {
        if (Operator.equals("IN")){
            if(StringHelper.isNullOrEmpty((String)Target))
                return "";
            return MessageFormat.format("{0} {1} {2}", Column, Operator, ((String)Target).replaceAll(",", "','").replaceAll(", ", "','").replaceAll("\\(", "('").replaceAll("\\)", "')"));
        }
        if (Operator.equals("LIKE"))
            return MessageFormat.format("{0} {1} ''%{2}%''", Column, Operator, Target);
        if (StringHelper.isNullOrEmpty(Order)) {
            if(Target.getClass().getName().toLowerCase().endsWith("int") ||
                    Target.getClass().getName().toLowerCase().endsWith("long") ||
                    Column.equalsIgnoreCase("ROWNUM"))
                return MessageFormat.format("{0} {1} ''{2}''", Column, Operator, Target).replace(",", "");
            return MessageFormat.format("{0} {1} ''{2}''", Column, Operator, Target);
        }
        else{
            if(Target.getClass().getName().toLowerCase().endsWith("int") ||
                    Target.getClass().getName().toLowerCase().endsWith("long") ||
                    Column.equalsIgnoreCase("ROWNUM"))
                return MessageFormat.format("{0} {1} ''{2}'' {3}", Column, Operator, Target, Order).replace(",", "");
            return MessageFormat.format("{0} {1} ''{2}'' {3}", Column, Operator, Target, Order);
        }
    }
}
