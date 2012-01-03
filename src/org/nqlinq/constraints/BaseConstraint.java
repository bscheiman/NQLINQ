package org.nqlinq.constraints;

public abstract class BaseConstraint {
    protected final String constraint;

    public BaseConstraint(String constraint) {
        this.constraint = constraint;
    }

    public abstract String getSql();
}
