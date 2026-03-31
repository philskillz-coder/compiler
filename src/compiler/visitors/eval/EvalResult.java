package compiler.visitors.eval;

import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.LiteralValue;
import compiler.visitors.eval.values.complex.FunctionValue;
import compiler.visitors.eval.values.complex.ObjectValue;
import compiler.visitors.eval.values.complex.ClassValue;
import compiler.parser.ast.BinaryOperator;
import compiler.parser.ast.UnaryOperator;
import compiler.parser.ast.FunctionDecl;
import compiler.visitors.eval.values.literal.BoolValue;
import compiler.visitors.eval.values.literal.FloatValue;
import compiler.visitors.eval.values.literal.IntValue;
import compiler.visitors.eval.values.literal.NullValue;
import compiler.visitors.eval.values.literal.StringValue;

import java.util.Map;

public class EvalResult {

    public enum ResultType {
        NORMAL,
        VALUE,
        RETURN,
        BREAK,
        CONTINUE
    }

    public final ResultType type;
    public final AbstractValue value;

    public EvalResult(ResultType type, AbstractValue value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "EvalResult{" +
                "type=" + type +
                ", value=" + (value != null ? value.getNativeAbstractValue() : "null") +
                '}';
    }

    // --- Factory Methods ---
    public static EvalResult normal() {
        return new EvalResult(ResultType.NORMAL, null);
    }

    public static EvalResult value(AbstractValue value) {
        return new EvalResult(ResultType.VALUE, value);
    }

    public static EvalResult returnValue(AbstractValue value) {
        return new EvalResult(ResultType.RETURN, value);
    }

    public static EvalResult breakStmt() {
        return new EvalResult(ResultType.BREAK, null);
    }

    public static EvalResult continueStmt() {
        return new EvalResult(ResultType.CONTINUE, null);
    }

    public boolean isBreaking() {
        return this.is(ResultType.RETURN, ResultType.BREAK, ResultType.CONTINUE);
    }

    public boolean is(ResultType t) {
        return this.type == t;
    }

    public boolean is(ResultType... types) {
        for (ResultType t : types) {
            if (this.type == t) return true;
        }
        return false;
    }

    public AbstractValue unwrapValue() {
        if (!is(ResultType.VALUE)) throw new EvalException("Expected VALUE type but got " + type);
        return value;
    }

    public AbstractValue unwrapReturnValue() {
        if (!is(ResultType.RETURN)) throw new EvalException("Expected RETURN type but got " + type);
        return value;
    }

    // --- Type Checks & Casts ---
    public boolean isObject() {
        return value instanceof ObjectValue;
    }

    public ObjectValue asObject() {
        if (!(value instanceof ObjectValue)) throw new EvalException("Value is not an object");
        return (ObjectValue) value;
    }

    public boolean isFunction() {
        return value instanceof FunctionValue;
    }

    public FunctionValue asFunction() {
        if (!(value instanceof FunctionValue)) throw new EvalException("Value is not a function");
        return (FunctionValue) value;
    }

    public boolean isClass() {
        return value instanceof ClassValue;
    }

    public ClassValue asClass() {
        if (!(value instanceof ClassValue)) throw new EvalException("Value is not a class");
        return (ClassValue) value;
    }

    // --- Operations ---
    public EvalResult applyBinary(BinaryOperator op, EvalResult rhs) {
        if (value == null || rhs.value == null) throw new EvalException("Binary operation on null");
        return new EvalResult(ResultType.VALUE, value.applyBinary(op, rhs.value));
    }

    public EvalResult applyUnary(UnaryOperator op) {
        if (value == null) throw new EvalException("Unary operation on null");
        return new EvalResult(ResultType.VALUE, value.applyUnary(op));
    }

    // --- Default Values ---
    public static EvalResult defaultValue(String typeName) {
        switch (typeName) {
            case "int": return value(new IntValue(0));
            case "float": return value(new FloatValue(0.0f));
            case "bool": return value(new BoolValue(false));
            case "string": return value(new StringValue(""));
            default: return value(new ObjectValue()); // Default für Klassenobjekte
        }
    }

    public static EvalResult nullValue() {
        return new EvalResult(ResultType.VALUE, NullValue.getInstance());
    }

    public AbstractValue asBoolean() {
        return value.asBoolean();
    }

    public static EvalResult fromInt(int val) {
        return new EvalResult(ResultType.VALUE, new IntValue(val));
    }

    public static EvalResult fromFloat(float val) {
        return new EvalResult(ResultType.VALUE, new FloatValue(val));
    }

    public static EvalResult fromBool(boolean val) {
        return new EvalResult(ResultType.VALUE, new BoolValue(val));
    }

    public static EvalResult fromString(String val) {
        return new EvalResult(ResultType.VALUE, new StringValue(val));
    }
}