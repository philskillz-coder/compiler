package compiler.visitors.eval.values;

import compiler.parser.ast.BinaryOperator;
import compiler.parser.ast.UnaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public abstract class AbstractValue implements NumericOperations, LogicOperations, ComparisonOperations, ConversionOperations {
    public abstract Object getNativeAbstractValue();

    @Override
    public String toString() {
        return String.valueOf(getNativeAbstractValue());
    }

    // --- NumericOperations / LogicOperations ---
    @Override public AbstractValue add(AbstractValue other) { throw binaryUnsupported("+", other); }
    @Override public AbstractValue subtract(AbstractValue other) { throw binaryUnsupported("-", other); }
    @Override public AbstractValue multiply(AbstractValue other) { throw binaryUnsupported("*", other); }
    @Override public AbstractValue divide(AbstractValue other) { throw binaryUnsupported("/", other); }
    @Override public AbstractValue modulo(AbstractValue other) { throw binaryUnsupported("%", other); }
    @Override public AbstractValue power(AbstractValue other) { throw binaryUnsupported("**", other); }
    @Override public AbstractValue bitwiseAnd(AbstractValue other) { throw binaryUnsupported("&", other); }
    @Override public AbstractValue bitwiseOr(AbstractValue other) { throw binaryUnsupported("|", other); }
    @Override public AbstractValue bitwiseXor(AbstractValue other) { throw binaryUnsupported("^", other); }
    @Override public AbstractValue leftShift(AbstractValue other) { throw binaryUnsupported("<<", other); }
    @Override public AbstractValue rightShift(AbstractValue other) { throw binaryUnsupported(">>", other); }
    @Override public AbstractValue logicalAnd(AbstractValue other) { throw binaryUnsupported("&&", other); }
    @Override public AbstractValue logicalOr(AbstractValue other) { throw binaryUnsupported("||", other); }
    @Override public AbstractValue logicalNot() { throw unaryUnsupported("!"); }
    @Override public AbstractValue bitwiseNot() { throw unaryUnsupported("~"); }
    @Override public AbstractValue negate() { throw unaryUnsupported("-"); }
    @Override public AbstractValue smallerThan(AbstractValue other) { throw binaryUnsupported("<", other); }
    @Override public AbstractValue smallerThanOrEqual(AbstractValue other) { throw binaryUnsupported("<=", other); }
    @Override public AbstractValue greaterThan(AbstractValue other) { throw binaryUnsupported(">", other); }
    @Override public AbstractValue greaterThanOrEqual(AbstractValue other) { throw binaryUnsupported(">=", other); }
    @Override public AbstractValue equalTo(AbstractValue other) { throw binaryUnsupported("==", other); }
    @Override public AbstractValue notEqualTo(AbstractValue other) { throw binaryUnsupported("!=", other); }
    @Override public AbstractValue asBoolean() { throw unaryUnsupported("asBoolean"); }

    // --- Neue Dispatch Methoden für EvalResult ---
    public AbstractValue applyBinary(BinaryOperator op, AbstractValue other) {
        switch (op) {
            case ADD: return add(other);
            case SUB: return subtract(other);
            case MUL: return multiply(other);
            case DIV: return divide(other);
            case MOD: return modulo(other);
            case POWER: return power(other);
            case BITWISE_AND: return bitwiseAnd(other);
            case BITWISE_OR: return bitwiseOr(other);
            case BITWISE_XOR: return bitwiseXor(other);
            case LEFT_SHIFT: return leftShift(other);
            case RIGHT_SHIFT: return rightShift(other);
            case LOGICAL_AND: return logicalAnd(other);
            case LOGICAL_OR: return logicalOr(other);
            case EQUAL: return equalTo(other);
            case NOT_EQUAL: return notEqualTo(other);
            case LESS: return smallerThan(other);
            case GREATER: return greaterThan(other);
            case LESS_EQUAL: return smallerThanOrEqual(other);
            case GREATER_EQUAL: return greaterThanOrEqual(other);
            default:
                throw new EvalException("Unsupported binary operator: " + op);
        }
    }

    public AbstractValue applyUnary(UnaryOperator op) {
        switch (op) {
            case NEGATE: return negate();
            case LOGIC_NOT: return logicalNot();
            case BITWISE_NOT: return bitwiseNot();
            default:
                throw new EvalException("Unsupported unary operator: " + op);
        }
    }

    // --- Helper ---
    private EvalException binaryUnsupported(String op, AbstractValue other) {
        return new EvalException("Operator '" + op + "' not supported between " +
                this.getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
    }

    private EvalException unaryUnsupported(String op) {
        return new EvalException("Unary operator '" + op + "' not supported by " + this.getClass().getSimpleName());
    }
}
