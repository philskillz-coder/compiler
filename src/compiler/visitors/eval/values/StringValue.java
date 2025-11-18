package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public class StringValue extends LiteralValue {
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.value;
    }

    @Override
    public AbstractValue add(AbstractValue other) {
        return new StringValue(this.value + other.getNativeAbstractValue().toString());
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        String rhsString = other.getNativeAbstractValue().toString();

        switch (op) {
            case EQUAL:
                return new BooleanValue(this.value.equals(rhsString));
            case NOT_EQUAL:
                return new BooleanValue(!this.value.equals(rhsString));

            case LESS:
                return new BooleanValue(this.value.compareTo(rhsString) < 0);
            case GREATER:
                return new BooleanValue(this.value.compareTo(rhsString) > 0);
            case LESS_EQUAL:
                return new BooleanValue(this.value.compareTo(rhsString) <= 0);
            case GREATER_EQUAL:
                return new BooleanValue(this.value.compareTo(rhsString) >= 0);

            default:
                throw new EvalException("Unsupported comparison operator '" + op + "' for String.");
        }
    }

}