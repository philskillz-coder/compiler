package compiler.visitors.eval.values.literal;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

public class BoolValue extends AbstractValue {
    private final boolean value;

    public BoolValue(boolean value) { this.value = value; }
    public boolean getValue() { return value; }

    @Override
    public Object getNativeAbstractValue() { return value; }

    @Override
    public AbstractValue logicalNot() { return new BoolValue(!value); }
    @Override
    public AbstractValue logicalAnd(AbstractValue other) {
        return new BoolValue(this.value && ((BoolValue) other).value);
    }
    @Override
    public AbstractValue logicalOr(AbstractValue other) {
        return new BoolValue(this.value || ((BoolValue) other).value);
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        boolean rhs = ((BoolValue) other).value;
        switch (op) {
            case EQUAL: return new BoolValue(this.value == rhs);
            case NOT_EQUAL: return new BoolValue(this.value != rhs);
            default: throw new EvalException("Invalid comparison operator for boolean: " + op);
        }
    }

    @Override
    public AbstractValue asBoolean() {
        return this;
    }
}