package org.nqlinq.constraints;

import org.nqlinq.helpers.StringHelper;

import java.text.MessageFormat;

public class OrderByDesc extends BaseConstraint {
    public OrderByDesc(String constraint) {
        super(constraint);
    }

    @Override
    public String getSql() {
        return MessageFormat.format("ORDER BY {0} DESC", constraint);
    }

    @Override
    public String toString() {
        return getSql();
    }
}