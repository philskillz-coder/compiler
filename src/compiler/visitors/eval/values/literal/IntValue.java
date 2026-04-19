package compiler.visitors.eval.values.literal;

import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.NumericValue;
import compiler.visitors.eval.exceptions.EvalException;

public class IntValue extends NumericValue {

    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // --- Core Logic ---

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public double asDouble() {
        return (double) value;
    }

    @Override
    protected AbstractValue getPreparedRHS(NumericValue rhs) {
        // Zwingt die rechte Seite zum Int (Left-Dominance)
        return new IntValue(rhs.asInt());
    }

    @Override
    public Object getNativeAbstractValue() {
        return value;
    }

    // --- Arithmetische Operationen ---

    @Override
    public AbstractValue add(AbstractValue other) {
        return new IntValue(this.value + ((IntValue) other).value);
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        return new IntValue(this.value - ((IntValue) other).value);
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        return new IntValue(this.value * ((IntValue) other).value);
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        int rhs = ((IntValue) other).value;
        if (rhs == 0) throw new EvalException("Division by zero");
        return new IntValue(this.value / rhs);
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        int rhs = ((IntValue) other).value;
        if (rhs == 0) throw new EvalException("Modulo by zero");
        return new IntValue(this.value % rhs);
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        int rhs = ((IntValue) other).value;
        return new IntValue((int) Math.pow(this.value, rhs));
    }

    // --- Bitweise Operationen ---

    @Override
    public AbstractValue bitwiseAnd(AbstractValue other) {
        return new IntValue(this.value & ((IntValue) other).value);
    }

    @Override
    public AbstractValue bitwiseOr(AbstractValue other) {
        return new IntValue(this.value | ((IntValue) other).value);
    }

    @Override
    public AbstractValue bitwiseXor(AbstractValue other) {
        return new IntValue(this.value ^ ((IntValue) other).value);
    }

    @Override
    public AbstractValue bitwiseNot() {
        return new IntValue(~this.value);
    }

    @Override
    public AbstractValue leftShift(AbstractValue other) {
        return new IntValue(this.value << ((IntValue) other).value);
    }

    @Override
    public AbstractValue rightShift(AbstractValue other) {
        return new IntValue(this.value >> ((IntValue) other).value);
    }

    // --- Vergleiche ---

    @Override
    public AbstractValue smallerThan(AbstractValue other) {
        return new BoolValue(this.value < ((IntValue) other).value);
    }

    @Override
    public AbstractValue smallerThanOrEqual(AbstractValue other) {
        return new BoolValue(this.value <= ((IntValue) other).value);
    }

    @Override
    public AbstractValue greaterThan(AbstractValue other) {
        return new BoolValue(this.value > ((IntValue) other).value);
    }

    @Override
    public AbstractValue greaterThanOrEqual(AbstractValue other) {
        return new BoolValue(this.value >= ((IntValue) other).value);
    }

    @Override
    public AbstractValue equalTo(AbstractValue other) {
        return new BoolValue(this.value == ((IntValue) other).value);
    }

    @Override
    public AbstractValue notEqualTo(AbstractValue other) {
        return new BoolValue(this.value != ((IntValue) other).value);
    }

    // --- Unäre Operationen ---

    @Override
    public AbstractValue negate() {
        return new IntValue(-this.value);
    }

    // --- Konvertierung ---

    @Override
    public AbstractValue asBoolean() {
        return new BoolValue(this.value != 0);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}