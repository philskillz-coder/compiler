package compiler.ast;

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


    POWER // für **
}
