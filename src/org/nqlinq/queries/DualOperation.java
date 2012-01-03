package org.nqlinq.queries;

import java.text.MessageFormat;

public class DualOperation {
    private final Operation Left;
    private final DualOperation LeftDual;
    private final Operation Right;
    private final DualOperation RightDual;
    private final String Verb;

    public DualOperation(Operation left, String verb, Operation right) {
        Left = left;
        Right = right;
        Verb = verb;
        LeftDual = null;
        RightDual = null;
    }

    public DualOperation(DualOperation left, String verb, Operation right) {
        Left = null;
        Right = right;
        Verb = verb;
        LeftDual = left;
        RightDual = null;
    }

    public DualOperation(Operation left, String verb, DualOperation right) {
        Left = left;
        Right = null;
        Verb = verb;
        LeftDual = null;
        RightDual = right;
    }

    public DualOperation(DualOperation left, String verb, DualOperation right) {
        Left = null;
        Right = null;
        Verb = verb;
        LeftDual = left;
        RightDual = right;
    }

    public DualOperation and(Operation target) {
        return new DualOperation(this, "AND", target);
    }

    public DualOperation or(Operation target) {
        return new DualOperation(this, "OR", target);
    }

    public DualOperation and(DualOperation target) {
        return new DualOperation(this, "AND", target);
    }

    public DualOperation or(DualOperation target) {
        return new DualOperation(this, "OR", target);
    }

    @Override
    public String toString() {
        if (Left != null && Right != null)
            return MessageFormat.format("{0} {1} {2}", Left, Verb, Right);

        if (LeftDual != null && Right != null)
            return MessageFormat.format("{0} {1} {2}", LeftDual, Verb, Right);

        if (Left != null && RightDual != null)
            return MessageFormat.format("{0} {1} {2}", Left, Verb, RightDual);

        else if (LeftDual != null && RightDual != null)
            return MessageFormat.format("{0} {1} {2}", LeftDual, Verb, RightDual);

        return null;
    }
}
