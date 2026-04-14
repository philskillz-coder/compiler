package compiler.visitors.eval.values.literal;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

public class IntValue extends AbstractValue {
    private final int value;

    public IntValue(int value) { this.value = value; }
    public int getValue() { return value; }

    @Override
    public Object getNativeAbstractValue() { return value; }

    @Override
    public AbstractValue add(AbstractValue other) {
        if (other instanceof IntValue) return new IntValue(this.value + ((IntValue) other).value);
        if (other instanceof FloatValue) return new FloatValue(this.value + ((FloatValue) other).getValue());
        throw new EvalException("Cannot add " + this.getClass() + " and " + other.getClass());
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        if (other instanceof IntValue) return new IntValue(this.value - ((IntValue) other).value);
        if (other instanceof FloatValue) return new FloatValue(this.value - ((FloatValue) other).getValue());
        throw new EvalException("Cannot subtract " + this.getClass() + " and " + other.getClass());
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        if (other instanceof IntValue) return new IntValue(this.value * ((IntValue) other).value);
        if (other instanceof FloatValue) return new FloatValue(this.value * ((FloatValue) other).getValue());
        throw new EvalException("Cannot multiply " + this.getClass() + " and " + other.getClass());
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        if (other instanceof IntValue) return new IntValue(this.value / ((IntValue) other).value);
        if (other instanceof FloatValue) return new FloatValue(this.value / ((FloatValue) other).getValue());
        throw new EvalException("Cannot divide " + this.getClass() + " and " + other.getClass());
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        return super.modulo(other);
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        return super.power(other);
    }

    @Override
    public AbstractValue bitwiseAnd(AbstractValue other) {
        return super.bitwiseAnd(other);
    }

    @Override
    public AbstractValue bitwiseOr(AbstractValue other) {
        return super.bitwiseOr(other);
    }

    @Override
    public AbstractValue bitwiseXor(AbstractValue other) {
        return super.bitwiseXor(other);
    }

    @Override
    public AbstractValue leftShift(AbstractValue other) {
        return super.leftShift(other);
    }

    @Override
    public AbstractValue rightShift(AbstractValue other) {
        return super.rightShift(other);
    }

    @Override
    public AbstractValue logicalAnd(AbstractValue other) {
        return super.logicalAnd(other);
    }

    @Override
    public AbstractValue logicalOr(AbstractValue other) {
        return super.logicalOr(other);
    }

    @Override
    public AbstractValue logicalNot() {
        return super.logicalNot();
    }

    @Override
    public AbstractValue bitwiseNot() {
        return super.bitwiseNot();
    }

    @Override
    public AbstractValue pre_increment() {
        return new IntValue(value+1);
    }

    @Override
    public AbstractValue post_increment() {
        return super.post_increment();
    }

    @Override
    public AbstractValue pre_decrement() {
        return new IntValue(value-1);
    }

    @Override
    public AbstractValue post_decrement() {
        return super.post_decrement();
    }

    @Override
    public AbstractValue negate() {
        return new IntValue(-value);
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        int rhs = ((IntValue) other).value;
        switch (op) {
            case EQUAL: return new BoolValue(this.value == rhs);
            case NOT_EQUAL: return new BoolValue(this.value != rhs);
            case LESS: return new BoolValue(this.value < rhs);
            case LESS_EQUAL: return new BoolValue(this.value <= rhs);
            case GREATER: return new BoolValue(this.value > rhs);
            case GREATER_EQUAL: return new BoolValue(this.value >= rhs);
            default: throw new EvalException("Invalid comparison operator " + op);
        }
    }

    @Override
    public AbstractValue asBoolean() {
        return new BoolValue(this.value != 0);
    }
}