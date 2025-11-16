package compiler.visitors.eval.values;

public interface LogicOperations {
    AbstractValue logicalAnd(AbstractValue other);
    AbstractValue logicalOr(AbstractValue other);
    AbstractValue logicalNot();
}
