package org.nqlinq.commands;

import org.nqlinq.constraints.BaseConstraint;

public class CountCommand extends BaseCommand {
    public CountCommand(String table, BaseConstraint... constraints) {
        super(table, constraints);
    }

    @Override
    public String getSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) AS COUNT FROM ");
        sb.append(table);

        if (constraints.length > 0)
            sb.append(" ");

        for (BaseConstraint constraint : constraints)
            sb.append(constraint.getSql());

        return sb.toString();
    }
}
