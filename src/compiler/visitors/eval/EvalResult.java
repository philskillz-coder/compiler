package compiler.visitors.eval;

import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

public class EvalResult {
    public enum ResultType {
        NORMAL, // Normal execution flow (e.g. if-else, loops)
        VALUE, // Simple value evaluation (Expr)
        RETURN, // Ending statement with value
        BREAK, // Break statement
        CONTINUE // Continue statement;
    }

    public final ResultType type;
    public final AbstractValue value;

    public EvalResult(ResultType type, AbstractValue value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "EvalResult{" +
                "type=" + type +
                ", value=" + value.getNativeAbstractValue().toString() +
                '}';
    }

    public static EvalResult normal() {
        return new EvalResult(ResultType.NORMAL, null);
    }

    public static EvalResult value(AbstractValue value) {
        return new EvalResult(ResultType.VALUE, value);
    }

    public static EvalResult returnValue(AbstractValue value) {
        return new EvalResult(ResultType.RETURN, value);
    }

    public static EvalResult breakStmt() {
        return new EvalResult(ResultType.BREAK, null);
    }

    public static EvalResult continueStmt() {
        return new EvalResult(ResultType.CONTINUE, null);
    }

    public boolean isBreaking() {
        return this.is(ResultType.RETURN, ResultType.BREAK, ResultType.CONTINUE);
    }

    public boolean is(ResultType t) {
        return this.type == t;
    }

    public boolean is(ResultType... types) {
        for (ResultType t : types) {
            if (this.type == t) {
                return true;
            }
        }
        return false;
    }

    public AbstractValue unwrapValue() {
        if (this.type != ResultType.VALUE) {
            throw new EvalException("Expected VALUE type but got " + this.type);
        }
        return this.value;
    }

    public AbstractValue unwrapReturnValue() {
        if (this.type != ResultType.RETURN) {
            throw new EvalException("Expected RETURN type but got " + this.type);
        }
        return this.value;
    }

}
