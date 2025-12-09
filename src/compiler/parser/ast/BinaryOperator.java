package compiler.parser.ast;

public enum BinaryOperator {
    LOGICAL_AND,
    LOGICAL_OR,

    // booleans
    EQUAL,         // für ==
    NOT_EQUAL,     // für !=
    LESS,          // für <
    GREATER,       // für >
    LESS_EQUAL,    // für <=
    GREATER_EQUAL, // für >=

    // arithmetic
    ADD,  // für +
    SUB,  // für -
    MUL,  // für *
    DIV,  // für /
    MOD,  // für % (Modulo)

    // arithmetic (bitwise)
    BITWISE_AND, // für &
    BITWISE_OR,  // für |
    BITWISE_XOR, // für ^ (XOR)
    LEFT_SHIFT,  // für <<
    RIGHT_SHIFT, // für >>


    POWER; // für **

    public boolean isArithmeticOrBitwise() {
        if (this == ADD || this == SUB || this == MUL || this == DIV || this == MOD) {
            return true;
        }
        if (this == BITWISE_AND || this == BITWISE_OR || this == BITWISE_XOR || this == LEFT_SHIFT || this == RIGHT_SHIFT || this == POWER) {
            return true;
        }
        return false;
    }

    public boolean isLogical() {
        return this == LOGICAL_AND || this == LOGICAL_OR;
    }

    public boolean isComparison() {
        if (this == EQUAL || this == NOT_EQUAL) {
            return true;
        }
        if (this == LESS || this == GREATER || this == LESS_EQUAL || this == GREATER_EQUAL) {
            return true;
        }
        return false;
    }
}
