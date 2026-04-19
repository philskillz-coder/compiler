package compiler.visitors.eval.values;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public abstract class NumericValue extends AbstractValue {
    public abstract int asInt();
    public abstract double asDouble();

    protected abstract AbstractValue getPreparedRHS(NumericValue rhs);

    @Override
    public AbstractValue applyBinary(BinaryOperator op, AbstractValue other) {
        if (!(other instanceof NumericValue)) {
            throw new EvalException("Operator '" + op + "' requires numeric types, but got " + this.getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
        }
        NumericValue rhs = (NumericValue) other;

        if (op.isBitwise()) {
            if (this.getClass() != rhs.getClass()) {
                throw new EvalException("Bitwise operator '" + op + "' requires same types (both "
                        + this.getClass().getSimpleName() + " or both " + rhs.getClass().getSimpleName() + ")");
            }
            return super.applyBinary(op, rhs);
        }

        if (this.getClass() == rhs.getClass()) {
            return super.applyBinary(op, rhs);
        }

        AbstractValue preparedRhs = getPreparedRHS(rhs);
        return super.applyBinary(op, preparedRhs);
    }
}