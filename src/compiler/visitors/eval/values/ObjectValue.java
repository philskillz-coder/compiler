package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends ComplexValue {
    public static class ObjectFunction {
        final String identifier;

        public ObjectFunction(String identifier) {
            this.identifier = identifier;
        }
    }

    private final Map<String, AbstractValue> properties = new HashMap<>();
    private final Map<String, ObjectFunction> subroutines = new HashMap<>();

    public ObjectValue() {

    }

    public void setProperty(String name, AbstractValue value) {
        properties.put(name, value);
    }

    public AbstractValue getProperty(String name) {
        return properties.get(name);
    }

    public void setSubroutine(String name, ObjectFunction function) {
        subroutines.put(name, function);
    }

    public ObjectFunction getSubroutine(String name) {
        return subroutines.get(name);
    }

    @Override
    public Object getNativeAbstractValue() {
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        return super.compare(op, other);
    }

    @Override
    public AbstractValue add(AbstractValue other) {
        return super.add(other);
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        return super.subtract(other);
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        return super.multiply(other);
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        return super.divide(other);
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
    public AbstractValue negate() {
        return super.negate();
    }
}
