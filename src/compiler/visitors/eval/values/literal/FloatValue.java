package compiler.visitors.eval.values.literal;

import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.NumericValue;
import compiler.visitors.eval.exceptions.EvalException;

public class FloatValue extends NumericValue {

    private final double value;

    public FloatValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    // --- Core Logic ---

    @Override
    public int asInt() {
        return (int) value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    protected AbstractValue getPreparedRHS(NumericValue rhs) {
        // Left-Dominance: Da "this" ein FloatValue ist,
        // wird die rechte Seite zu einem FloatValue konvertiert.
        return new FloatValue(rhs.asDouble());
    }

    @Override
    public Object getNativeAbstractValue() {
        return value;
    }

    // --- Arithmetische Operationen ---

    @Override
    public AbstractValue add(AbstractValue other) {
        return new FloatValue(this.value + ((FloatValue) other).value);
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        return new FloatValue(this.value - ((FloatValue) other).value);
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        return new FloatValue(this.value * ((FloatValue) other).value);
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        double rhs = ((FloatValue) other).value;
        if (rhs == 0.0) throw new EvalException("Division by zero");
        return new FloatValue(this.value / rhs);
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        double rhs = ((FloatValue) other).value;
        if (rhs == 0.0) throw new EvalException("Modulo by zero");
        return new FloatValue(this.value % rhs);
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        double rhs = ((FloatValue) other).value;
        return new FloatValue(Math.pow(this.value, rhs));
    }

    // --- Vergleiche ---

    @Override
    public AbstractValue smallerThan(AbstractValue other) {
        return new BoolValue(this.value < ((FloatValue) other).value);
    }

    @Override
    public AbstractValue smallerThanOrEqual(AbstractValue other) {
        return new BoolValue(this.value <= ((FloatValue) other).value);
    }

    @Override
    public AbstractValue greaterThan(AbstractValue other) {
        return new BoolValue(this.value > ((FloatValue) other).value);
    }

    @Override
    public AbstractValue greaterThanOrEqual(AbstractValue other) {
        return new BoolValue(this.value >= ((FloatValue) other).value);
    }

    @Override
    public AbstractValue equalTo(AbstractValue other) {
        return new BoolValue(this.value == ((FloatValue) other).value);
    }

    @Override
    public AbstractValue notEqualTo(AbstractValue other) {
        return new BoolValue(this.value != ((FloatValue) other).value);
    }

    // --- Unäre Operationen ---

    @Override
    public AbstractValue negate() {
        return new FloatValue(-this.value);
    }

    // --- Konvertierung ---

    @Override
    public AbstractValue asBoolean() {
        // In vielen Sprachen ist 0.0 false, alles andere true
        return new BoolValue(this.value != 0.0);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}