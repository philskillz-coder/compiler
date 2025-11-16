package compiler.visitors.eval.values;

import compiler.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;

public class NullValue extends AbstractValue {

    private static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    public static NullValue getInstance() {
        return INSTANCE;
    }

    @Override
    public Object getNativeAbstractValue() {
        return null;
    }

    @Override
    public AbstractValue compare(BinaryOperator op, AbstractValue other) {
        boolean isOtherNull = (other instanceof NullValue);

        switch (op) {
            case EQUAL:
                return new BooleanValue(isOtherNull);

            case NOT_EQUAL:
                return new BooleanValue(!isOtherNull);

            default:
                throw new EvalException("Unsupported comparison operator '" + op + "' for Null.");
        }
    }
}