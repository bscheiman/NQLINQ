package org.nqlinq.constraints;

import org.nqlinq.helpers.StringHelper;

import java.text.MessageFormat;

public class OrderBy extends BaseConstraint {
    public OrderBy(String constraint) {
        super(constraint);
    }

    @Override
    public String getSql() {
        return MessageFormat.format("ORDER BY {0}", constraint);
    }

    @Override
    public String toString() {
        return getSql();
    }
}
