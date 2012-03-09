package org.nqlinq.commands;

import org.nqlinq.constraints.BaseConstraint;

public abstract class BaseCommand {
    protected final String table;
    protected final BaseConstraint[] constraints;

    public BaseCommand(String table, BaseConstraint... constraints) {
        this.table = table;
        this.constraints = constraints;
    }

    public abstract String getSql();

    public BaseConstraint[] getConstraints(){
        return constraints;
    }
}
