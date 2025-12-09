package compiler.visitors.eval.values.literal;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.LiteralValue;

public class FloatValue extends LiteralValue {
    private final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.value;
    }

    private float unwrap(AbstractValue other) {
        if (other instanceof FloatValue) {
            return ((FloatValue) other).value;
        }
        throw new EvalException("Operation not supported: Second operand is not a float.");
    }

    @Override
    public AbstractValue add(AbstractValue other) {
        if (other instanceof FloatValue) {
            return new FloatValue(this.value + unwrap(other));
        }
        // HIER MÜSSTE DIE LOGIK FÜR FLOAT + INTEGER IMPLEMENTIERT WERDEN
        throw new EvalException("Operator '+' not supported between Float and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        if (other instanceof FloatValue) {
            return new FloatValue(this.value - unwrap(other));
        }
        throw new EvalException("Operator '-' not supported between Float and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        if (other instanceof FloatValue) {
            return new FloatValue(this.value * unwrap(other));
        }
        throw new EvalException("Operator '*' not supported between Float and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        float rhs = unwrap(other);
        if (rhs == 0.0f) {
            // Im Gegensatz zu Integer-Division führt Java hier zu Infinity, aber wir werfen
            throw new EvalException("Runtime error: Division by zero.");
        }
        return new FloatValue(this.value / rhs);
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        return new FloatValue(this.value % unwrap(other));
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        return new FloatValue((float) Math.pow(this.value, unwrap(other)));
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        if (!op.isComparison()) {
            throw new EvalException("Operator '" + op + "' is not a comparison operator.");
        }

        if (other instanceof FloatValue) {
            float rhs = unwrap(other);
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
            return new BooleanValue(result);
        }
        throw new EvalException("Comparison not supported between Float and " + other.getClass().getSimpleName());
    }
}