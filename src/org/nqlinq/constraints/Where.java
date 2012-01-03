package org.nqlinq.constraints;

import java.text.MessageFormat;

public class Where extends BaseConstraint {
    public Where(String constraint) {
        super(constraint);
    }

    @Override
    public String getSql() {
        return MessageFormat.format("WHERE {0}", constraint);
    }
}
