package org.nqlinq.queries;

import org.nqlinq.helpers.StringHelper;

import java.text.MessageFormat;

public class Operation {
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
        if (StringHelper.isNullOrEmpty(Order))
            return MessageFormat.format("{0} {1} ''{2}''", Column, Operator, Target);
        else
            return MessageFormat.format("{0} {1} ''{2}'' {3}", Column, Operator, Target, Order);
    }
}
