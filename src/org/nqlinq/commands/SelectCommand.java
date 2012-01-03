package org.nqlinq.commands;

import org.nqlinq.constraints.BaseConstraint;

import java.text.MessageFormat;

public class SelectCommand extends BaseCommand {
    public SelectCommand(String table, BaseConstraint... constraints) {
        super(table, constraints);
    }

    @Override
    public String getSql() {
        StringBuilder sb = new StringBuilder(MessageFormat.format("SELECT * FROM {0}", table));

        if (constraints.length > 0)
            sb.append(" ");
        
        for (BaseConstraint constraint : constraints)
            sb.append(constraint.getSql());

        return sb.toString();
    }
}
