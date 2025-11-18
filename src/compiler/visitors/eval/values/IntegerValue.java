package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public class IntegerValue extends LiteralValue {
    private final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.value;
    }

    private int unwrap(AbstractValue other) {
        if (other instanceof IntegerValue) {
            return ((IntegerValue) other).value;
        }
        throw new EvalException("Operation not supported: Second operand is not an integer.");
    }

    @Override
    public AbstractValue add(AbstractValue other) {
        // todo: implement FloatValue and handle promotion
        if (other instanceof IntegerValue) {
            return new IntegerValue(this.value + unwrap(other));
        }
        if (other instanceof FloatValue) {
            return new FloatValue(this.value + (float) other.getNativeAbstractValue());
        }
        if (other instanceof StringValue) {
            return new StringValue(this.value + (String) other.getNativeAbstractValue());
        }

        throw new EvalException("Operator '+' not supported between Integer and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        if (other instanceof IntegerValue) {
            return new IntegerValue(this.value - unwrap(other));
        }
        if (other instanceof FloatValue) {
            return new FloatValue(this.value - (float) other.getNativeAbstractValue());
        }

        throw new EvalException("Operator '-' not supported between Integer and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        if (other instanceof IntegerValue) {
            return new IntegerValue(this.value * unwrap(other));
        }
        if (other instanceof FloatValue) {
            return new FloatValue(this.value * (float) other.getNativeAbstractValue());
        }
        if (other instanceof StringValue) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.value; i++) {
                sb.append(other.getNativeAbstractValue());
            }
            return new StringValue(sb.toString());
        }
        throw new EvalException("Operator '*' not supported between Integer and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        if (other instanceof IntegerValue) {
            int rhs = unwrap(other);
            if (rhs == 0) {
                throw new EvalException("Runtime error: Division by zero.");
            }

            return new IntegerValue(this.value / rhs);
        }
        if (other instanceof FloatValue) {
            float rhs = (float) other.getNativeAbstractValue();
            if (rhs == 0.0) {
                throw new EvalException("Runtime error: Division by zero.");
            }

            return new FloatValue(this.value / rhs);
        }

        throw new EvalException("Operator '/' not supported between Integer and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        int rhs = unwrap(other);
        return new IntegerValue(this.value % rhs);
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        int rhs = unwrap(other);
        return new IntegerValue((int) Math.pow(this.value, rhs));
    }

    @Override
    public AbstractValue bitwiseAnd(AbstractValue other) {
        return new IntegerValue(this.value & unwrap(other));
    }

    @Override
    public AbstractValue bitwiseOr(AbstractValue other) {
        return new IntegerValue(this.value | unwrap(other));
    }

    @Override
    public AbstractValue bitwiseXor(AbstractValue other) {
        return new IntegerValue(this.value ^ unwrap(other));
    }

    @Override
    public AbstractValue leftShift(AbstractValue other) {
        return new IntegerValue(this.value << unwrap(other));
    }

    @Override
    public AbstractValue rightShift(AbstractValue other) {
        return new IntegerValue(this.value >> unwrap(other));
    }

    // --- Vergleichsoperationen ---

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        if (other instanceof IntegerValue) {
            int rhs = unwrap(other);
            boolean result;

            switch (op) {
                case EQUAL: result = this.value == rhs; break;
                case NOT_EQUAL: result = this.value != rhs; break;
                case LESS: result = this.value < rhs; break;
                case GREATER: result = this.value > rhs; break;
                case LESS_EQUAL: result = this.value <= rhs; break;
                case GREATER_EQUAL: result = this.value >= rhs; break;
                default:
                    throw new EvalException("Unsupported comparison operator: " + op);
            }
            // Vergleiche geben BooleanValue zurück
            return new BooleanValue(result);
        }
        throw new EvalException("Comparison not supported between Integer and " + other.getClass().getSimpleName());
    }
}