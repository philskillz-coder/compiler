package compiler.visitors.eval.values;

public interface ComparisonOperations {
    AbstractValue smallerThan(AbstractValue other);
    AbstractValue smallerThanOrEqual(AbstractValue other);
    AbstractValue greaterThan(AbstractValue other);
    AbstractValue greaterThanOrEqual(AbstractValue other);
    AbstractValue equalTo(AbstractValue other);
    AbstractValue notEqualTo(AbstractValue other);
}
