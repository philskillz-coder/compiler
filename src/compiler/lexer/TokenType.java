package compiler.lexer;

/**
 * Definiert alle möglichen Arten von Tokens, die vom Lexer erkannt werden.
 */
public enum TokenType {
    // I. LITERALE UND IDENTIFIER
    IDENTIFIER,       // Variable oder Funktionsname
    INT_LITERAL,      // Ganze Zahl (z.B. 123)
    STRING_LITERAL,   // Zeichenkette (z.B. "text")
    FLOAT_LITERAL,    // Dezimalzahl (z.B. 12.3)
    BOOL_LITERAL,     // Boolescher Wert (true/false)
    NULL_LITERAL,

    // II. ARITHMETISCHE & LOGISCHE OPERATOREN
    OP_ADD,             // +
    OP_SUBTRACT,        // - (auch unäre Negation)
    OP_MULTIPLY,        // *
    OP_DIVIDE,          // /
    OP_MODULO,          // %
    OP_POWER,           // ** (Exponent)

    // Logische Operatoren
    OP_LOGICAL_OR,      // ||
    OP_LOGICAL_AND,     // &&
    OP_LOGICAL_NOT,     // ! (Unäres NICHT)

    // Zuweisung & Gleichheit
    OP_ASSIGN,          // =
    OP_EQUALS,          // ==
    OP_NOTEQUAL,        // !=

    // Vergleich
    OP_LESS_THAN,       // <
    OP_GREATER_THAN,    // >
    OP_LESS_EQUAL,      // <=
    OP_GREATER_EQUAL,   // >=

    // III. BITWEISE OPERATOREN
    OP_BITWISE_AND,     // &
    OP_BITWISE_OR,      // |
    OP_BITWISE_XOR,     // ^
    OP_BITWISE_NOT,     // ~ (Unäres Bit-NICHT)
    OP_LEFT_SHIFT,      // <<
    OP_RIGHT_SHIFT,     // >>

    // IV. INKREMENT/DEKREMENT (Unär)
    OP_INCREMENT,       // ++
    OP_DECREMENT,       // --

    // V. TRENNZEICHEN
    PAREN_OPEN,         // (
    PAREN_CLOSE,        // )
    CURLY_OPEN,         // {
    CURLY_CLOSE,        // }
    SEMICOLON,          // ;
    COMMA,              // ,
    PERIOD,             // . (Zugriffsoperator)

    // VI. SCHLÜSSELWÖRTER (KEYWORDS)
    KW_VAR,             // var
    KW_IF,              // if
    KW_ELSE,            // else
    KW_WHILE,           // while
    KW_BREAK,
    KW_CONTINUE,
    KW_FUNC,            // func
    KW_RETURN,          // return
    KW_YIELD,
    KW_CLASS,           // class
    KW_PUBLIC,
    KW_PRIVATE,
    KW_FINAL,           // final
    KW_STATIC,          // static
    KW_TRY,             // try
    KW_CATCH,           // catch
    KW_FINALLY,         // finally

    // VII. ZUSTANDS-MARKER
    EOF,                // End Of File
    SOF                 // Start Of File (optional)
}