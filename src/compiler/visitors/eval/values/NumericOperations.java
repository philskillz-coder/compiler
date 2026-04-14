package compiler.visitors.eval.values;

public interface NumericOperations {

    AbstractValue add(AbstractValue other);
    AbstractValue subtract(AbstractValue other);
    AbstractValue multiply(AbstractValue other);
    AbstractValue divide(AbstractValue other);
    AbstractValue modulo(AbstractValue other);
    AbstractValue power(AbstractValue other); // Für **

    AbstractValue bitwiseAnd(AbstractValue other);
    AbstractValue bitwiseOr(AbstractValue other);
    AbstractValue bitwiseNot();
    AbstractValue bitwiseXor(AbstractValue other);
    AbstractValue leftShift(AbstractValue other);
    AbstractValue rightShift(AbstractValue other);

    AbstractValue pre_increment();
    AbstractValue post_increment();
    AbstractValue pre_decrement();
    AbstractValue post_decrement();

    AbstractValue negate();
}
