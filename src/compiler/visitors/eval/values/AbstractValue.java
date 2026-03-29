package compiler.visitors.eval.values;

import compiler.parser.ast.BinaryOperator;
import compiler.parser.ast.UnaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public abstract class AbstractValue implements NumericOperations, LogicOperations, ConversionOperations {

    public abstract Object getNativeAbstractValue();

    @Override
    public String toString() {
        return String.valueOf(getNativeAbstractValue());
    }

    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        if (!op.isComparison()) {
            throw new EvalException("Operator '" + op + "' is not a comparison operator.");
        }
        throw new EvalException(
                "Comparison operator '" + op + "' not supported by " + this.getClass().getSimpleName()
        );
    }

    // --- NumericOperations / LogicOperations ---
    @Override public AbstractValue add(AbstractValue other) { throw unsupported("+", other); }
    @Override public AbstractValue subtract(AbstractValue other) { throw unsupported("-", other); }
    @Override public AbstractValue multiply(AbstractValue other) { throw unsupported("*", other); }
    @Override public AbstractValue divide(AbstractValue other) { throw unsupported("/", other); }
    @Override public AbstractValue modulo(AbstractValue other) { throw unsupported("%", other); }
    @Override public AbstractValue power(AbstractValue other) { throw unsupported("**", other); }
    @Override public AbstractValue bitwiseAnd(AbstractValue other) { throw unsupported("&", other); }
    @Override public AbstractValue bitwiseOr(AbstractValue other) { throw unsupported("|", other); }
    @Override public AbstractValue bitwiseXor(AbstractValue other) { throw unsupported("^", other); }
    @Override public AbstractValue leftShift(AbstractValue other) { throw unsupported("<<", other); }
    @Override public AbstractValue rightShift(AbstractValue other) { throw unsupported(">>", other); }
    @Override public AbstractValue logicalAnd(AbstractValue other) { throw unsupported("&&", other); }
    @Override public AbstractValue logicalOr(AbstractValue other) { throw unsupported("||", other); }
    @Override public AbstractValue logicalNot() { throw unaryUnsupported("!"); }
    @Override public AbstractValue bitwiseNot() { throw unaryUnsupported("~"); }
    @Override public AbstractValue negate() { throw unaryUnsupported("-"); }
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
            case EQUAL:
            case NOT_EQUAL:
            case LESS:
            case GREATER:
            case LESS_EQUAL:
            case GREATER_EQUAL:
                return compare(op, other);
            default:
                throw new EvalException("Unsupported binary operator: " + op);
        }
    }

    public AbstractValue applyUnary(UnaryOperator op) {
        switch (op) {
            case NEGATE: return negate();
            case LOGIC_NOT: return logicalNot();
            case BITWISE_NOT: return bitwiseNot();
            case PRE_INC:
            case POST_INC:
            case PRE_DEC:
            case POST_DEC:
                throw new EvalException("Increment/Decrement not implemented yet for " + this.getClass().getSimpleName());
            default:
                throw new EvalException("Unsupported unary operator: " + op);
        }
    }

    // --- Helper ---
    private EvalException unsupported(String op, AbstractValue other) {
        return new EvalException("Operator '" + op + "' not supported between " +
                this.getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
    }

    private EvalException unaryUnsupported(String op) {
        return new EvalException("Unary operator '" + op + "' not supported by " + this.getClass().getSimpleName());
    }
}
