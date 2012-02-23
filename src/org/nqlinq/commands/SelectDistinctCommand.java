package org.nqlinq.commands;

import org.nqlinq.constraints.BaseConstraint;

import java.text.MessageFormat;

public class SelectDistinctCommand extends BaseCommand {
    String columnName;
    
    public SelectDistinctCommand(String table, String column, BaseConstraint... constraints) {
        super(table, constraints);
        columnName = column;
    }

    @Override
    public String getSql() {
        StringBuilder sb = new StringBuilder(MessageFormat.format("SELECT DISTINCT {0} FROM {1}",
                columnName, table));

        if (constraints.length > 0)
            sb.append(" ");

        for (BaseConstraint constraint : constraints)
            sb.append(constraint.getSql());

        return sb.toString();
    }
}
