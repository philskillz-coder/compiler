package compiler.ast;

public enum UnaryOperator {
    NEGATE,
    LOGIC_NOT,
    BITWISE_NOT,
    PRE_INC, POST_INC,
    PRE_DEC, POST_DEC;

    public boolean isIncrementOrDecrement() {
        if (this == PRE_INC || this == POST_INC) {
            return true;
        }
        if (this == PRE_DEC || this == POST_DEC) {
            return true;
        }
        return false;
    }
}
