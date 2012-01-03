package org.nqlinq.queries;

import org.nqlinq.exceptions.InvalidOperationException;

@SuppressWarnings({ "ALL" })
public class OperationBuilder {
    protected DualOperation CurrOperation;
    protected Operation StarterOperation;
    protected boolean IsBuilding = false;

    public OperationBuilder() {
    }

    public OperationBuilder(Operation oper) {
        try {
            begin(oper);
        } catch (InvalidOperationException e) {
        }
    }

    public OperationBuilder begin(Operation oper) throws InvalidOperationException {
        if (IsBuilding)
            throw new InvalidOperationException();

        StarterOperation = oper;

        IsBuilding = true;

        return this;
    }

    public OperationBuilder end() throws InvalidOperationException {
        if (!IsBuilding)
            throw new InvalidOperationException();

        IsBuilding = false;

        return this;
    }

    public OperationBuilder and(Operation oper) throws InvalidOperationException {
        if (StarterOperation == null)
            StarterOperation = oper;
        else if (CurrOperation == null)
            CurrOperation = StarterOperation.and(oper);
        else if (CurrOperation != null)
            CurrOperation = CurrOperation.and(oper);

        return this;
    }

    public OperationBuilder or(Operation oper) throws InvalidOperationException {
        if (StarterOperation == null)
            StarterOperation = oper;
        else if (CurrOperation == null)
            CurrOperation = StarterOperation.or(oper);
        else if (CurrOperation != null)
            CurrOperation = CurrOperation.or(oper);

        return this;
    }

    public DualOperation getOperation() throws InvalidOperationException {
        if (IsBuilding)
            throw new InvalidOperationException();

        if (CurrOperation == null)
            return StarterOperation.or(StarterOperation);

        return CurrOperation;
    }
}
