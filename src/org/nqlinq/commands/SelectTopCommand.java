package org.nqlinq.commands;

import org.nqlinq.constants.DbStrings;
import org.nqlinq.constraints.BaseConstraint;

import java.text.MessageFormat;

public class SelectTopCommand extends BaseCommand {
    int totalRecords = 0;
    public SelectTopCommand(String table, int count, BaseConstraint... constraints) {
        super(table, constraints);
        totalRecords = count;
    }

    @Override
    public String getSql() {
        StringBuilder sb = new StringBuilder(MessageFormat.format(DbStrings.SelectVal, table, totalRecords));

        if (constraints.length > 0)
            sb.append(" ");

        for (BaseConstraint constraint : constraints)
            sb.append(constraint.getSql());

        sb.append(MessageFormat.format(DbStrings.AppendVal, totalRecords));
        return sb.toString();
    }
}

