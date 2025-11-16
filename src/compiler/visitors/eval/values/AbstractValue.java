package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public abstract class AbstractValue implements NumericOperations, LogicOperations {
    // Muss von den Subklassen implementiert werden
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

    @Override
    public AbstractValue add(AbstractValue other) {
        throw new EvalException("Operator '+' not supported between " + this.getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
    }

    @Override
    public AbstractValue subtract(AbstractValue other) {
        throw new EvalException("Operator '-' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue multiply(AbstractValue other) {
        throw new EvalException("Operator '*' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue divide(AbstractValue other) {
        throw new EvalException("Operator '/' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue modulo(AbstractValue other) {
        throw new EvalException("Operator '%' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue power(AbstractValue other) {
        throw new EvalException("Operator '**' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue bitwiseAnd(AbstractValue other) {
        throw new EvalException("Operator '&' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue bitwiseOr(AbstractValue other) {
        throw new EvalException("Operator '|' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue bitwiseXor(AbstractValue other) {
        throw new EvalException("Operator '^' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue leftShift(AbstractValue other) {
        throw new EvalException("Operator '<<' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue rightShift(AbstractValue other) {
        throw new EvalException("Operator '>>' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue logicalAnd(AbstractValue other) {
        throw new EvalException("Operator '&&' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue logicalOr(AbstractValue other) {
        throw new EvalException("Operator '||' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue logicalNot() {
        throw new EvalException("Operator '!' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue bitwiseNot() {
        throw new EvalException("Operator '~' not supported by " + this.getClass().getSimpleName());
    }

    @Override
    public AbstractValue negate() {
        throw new EvalException("Unary '-' not supported by " + this.getClass().getSimpleName());
    }
}