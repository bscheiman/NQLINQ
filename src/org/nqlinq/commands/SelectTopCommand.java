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
        StringBuilder sb = new StringBuilder(DbStrings.SelectVal.replace("{0}", table).replace("{1}", totalRecords+""));

        if (constraints.length > 0)
            sb.append(" ");

        for (BaseConstraint constraint : constraints)
            sb.append(constraint.getSql());

        sb.append(DbStrings.AppendVal.replace("{0}", totalRecords+""));
        return sb.toString();
    }
}

