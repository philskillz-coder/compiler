package compiler.visitors.eval.values.literal;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.LiteralValue;

public class StringValue extends AbstractValue {
    private final String value;

    public StringValue(String value) { this.value = value; }

    @Override
    public Object getNativeAbstractValue() { return value; }

    @Override
    public AbstractValue add(AbstractValue other) {
        if (other instanceof StringValue || other instanceof IntValue || other instanceof FloatValue || other instanceof BoolValue) {
            return new StringValue(this.value + other.getNativeAbstractValue());
        }
        throw new EvalException("Cannot add string and " + other.getClass());
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        String rhs = other.getNativeAbstractValue().toString();
        switch (op) {
            case EQUAL: return new BoolValue(this.value.equals(rhs));
            case NOT_EQUAL: return new BoolValue(!this.value.equals(rhs));
            default: throw new EvalException("Invalid comparison operator for string: " + op);
        }
    }

    @Override
    public AbstractValue asBoolean() {
        return new BoolValue(!this.value.isEmpty());
    }
}