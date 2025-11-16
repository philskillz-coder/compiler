package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public class BooleanValue extends AbstractValue {
    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.value;
    }

    @Override
    public AbstractValue logicalAnd(AbstractValue other) {
        if (other instanceof BooleanValue) {
            return new BooleanValue(this.value && ((BooleanValue) other).value);
        }
        throw new EvalException("Operator '&&' not supported between Boolean and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue logicalOr(AbstractValue other) {
        if (other instanceof BooleanValue) {
            return new BooleanValue(this.value || ((BooleanValue) other).value);
        }
        throw new EvalException("Operator '||' not supported between Boolean and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        if (!op.isComparison()) {
            throw new EvalException("Operator '" + op + "' is not a comparison operator.");
        }

        if (other instanceof BooleanValue) {
            boolean rhs = ((BooleanValue) other).value;
            boolean result;

            switch (op) {
                case EQUAL:
                    result = this.value == rhs;
                    break;
                case NOT_EQUAL:
                    result = this.value != rhs;
                    break;
                default:
                    // Boolean unterstützt typischerweise keine LESS/GREATER Vergleiche
                    throw new EvalException("Unsupported comparison operator '" + op + "' for boolean.");
            }
            return new BooleanValue(result);
        }
        throw new EvalException("Comparison not supported between Boolean and " + other.getClass().getSimpleName());
    }
}