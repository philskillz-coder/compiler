package compiler.lexer;

public abstract class Token<T> {
    private final T value;
    private final TokenType type;

    public Token(T value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + type.name() + (value != null ? ": " + value : "") + "]";
    }
}

// Abstrakte Klasse für einfache Symbole und Keywords
abstract class TokenSymbol extends Token<String> {
    public TokenSymbol(String value, TokenType type) {
        super(value, type);
    }
}

// --- 1. Trennzeichen und Markierungen ---

class TokenCurlyOpen extends TokenSymbol {
    public TokenCurlyOpen() { super("{", TokenType.CURLY_OPEN); }
}

class TokenCurlyClose extends TokenSymbol {
    public TokenCurlyClose() { super("}", TokenType.CURLY_CLOSE); }
}

class TokenParenOpen extends TokenSymbol {
    public TokenParenOpen() { super("(", TokenType.PAREN_OPEN); }
}

class TokenParenClose extends TokenSymbol {
    public TokenParenClose() { super(")", TokenType.PAREN_CLOSE); }
}

class TokenSemicolon extends TokenSymbol {
    public TokenSemicolon() { super(";", TokenType.SEMICOLON); }
}

class TokenComma extends TokenSymbol {
    public TokenComma() { super(",", TokenType.COMMA); }
}

class TokenPeriod extends TokenSymbol {
    public TokenPeriod() { super(".", TokenType.PERIOD);}
}

class TokenEOF extends TokenSymbol {
    public TokenEOF() { super("EOF", TokenType.EOF); }
}

class TokenSOF extends TokenSymbol {
    public TokenSOF() { super("SOF", TokenType.SOF); }
}


// --- 2. Literale und Identifier (mit tatsächlichem Wert) ---

class TokenIdentifier extends Token<String> {
    public TokenIdentifier(String value) {
        super(value, TokenType.IDENTIFIER);
    }
}

class TokenLiteralInt extends Token<Integer> {
    public TokenLiteralInt(Integer value) {
        super(value, TokenType.INT_LITERAL);
    }
}

class TokenLiteralFloat extends Token<Double> {
    public TokenLiteralFloat(Double value) {
        super(value, TokenType.FLOAT_LITERAL);
    }
}

class TokenLiteralString extends Token<String> {
    public TokenLiteralString(String value) {
        super(value, TokenType.STRING_LITERAL);
    }
}

class TokenLiteralBool extends Token<Boolean> {
    public TokenLiteralBool(Boolean value) {
        super(value, TokenType.BOOL_LITERAL);
    }
}

class TokenLiteralNull extends Token<Object> {
    public TokenLiteralNull() {
        super(null, TokenType.NULL_LITERAL);
    }
}

// --- 3. Operatoren ---

abstract class TokenOperator extends TokenSymbol {
    public TokenOperator(String value, TokenType type) { super(value, type); }
}

class TokenOperatorAdd extends TokenOperator {
    public TokenOperatorAdd() { super("+", TokenType.OP_ADD); }
}

class TokenOperatorSub extends TokenOperator {
    public TokenOperatorSub() { super("-", TokenType.OP_SUBTRACT); }
}

class TokenOperatorMul extends TokenOperator {
    public TokenOperatorMul() { super("*", TokenType.OP_MULTIPLY); }
}

class TokenOperatorDiv extends TokenOperator {
    public TokenOperatorDiv() { super("/", TokenType.OP_DIVIDE); }
}

class TokenOperatorAssign extends TokenOperator {
    public TokenOperatorAssign() { super("=", TokenType.OP_ASSIGN); }
}

class TokenOperatorEqual extends TokenOperator {
    public TokenOperatorEqual() { super("==", TokenType.OP_EQUALS); }
}

class TokenOperatorNotEqual extends TokenOperator {
    public TokenOperatorNotEqual() { super("!=", TokenType.OP_NOTEQUAL); }
}

class TokenOperatorLess extends TokenOperator {
    public TokenOperatorLess() { super("<", TokenType.OP_LESS_THAN); }
}

class TokenOperatorGreater extends TokenOperator {
    public TokenOperatorGreater() { super(">", TokenType.OP_GREATER_THAN); }
}

class TokenOperatorLessEqual extends TokenOperator {
    public TokenOperatorLessEqual() { super("<=", TokenType.OP_LESS_EQUAL); }
}

class TokenOperatorGreaterEqual extends TokenOperator {
    public TokenOperatorGreaterEqual() { super(">=", TokenType.OP_GREATER_EQUAL); }
}

// Arithmetik
class TokenOperatorModulo extends TokenOperator {
    public TokenOperatorModulo() { super("%", TokenType.OP_MODULO); }
}

class TokenOperatorPower extends TokenOperator {
    public TokenOperatorPower() { super("**", TokenType.OP_POWER); }
}

// Logische Operatoren
class TokenOperatorLogicalOr extends TokenOperator {
    public TokenOperatorLogicalOr() { super("||", TokenType.OP_LOGICAL_OR); }
}

class TokenOperatorLogicalAnd extends TokenOperator {
    public TokenOperatorLogicalAnd() { super("&&", TokenType.OP_LOGICAL_AND); }
}

class TokenOperatorLogicalNot extends TokenOperator {
    public TokenOperatorLogicalNot() { super("!", TokenType.OP_LOGICAL_NOT); }
}

// Bitweise Operatoren
class TokenOperatorBitwiseAnd extends TokenOperator {
    public TokenOperatorBitwiseAnd() { super("&", TokenType.OP_BITWISE_AND); }
}

class TokenOperatorBitwiseOr extends TokenOperator {
    public TokenOperatorBitwiseOr() { super("|", TokenType.OP_BITWISE_OR); }
}

class TokenOperatorBitwiseXor extends TokenOperator {
    public TokenOperatorBitwiseXor() { super("^", TokenType.OP_BITWISE_XOR); }
}

class TokenOperatorBitwiseNot extends TokenOperator {
    public TokenOperatorBitwiseNot() { super("~", TokenType.OP_BITWISE_NOT); }
}

class TokenOperatorLeftShift extends TokenOperator {
    public TokenOperatorLeftShift() { super("<<", TokenType.OP_LEFT_SHIFT); }
}

class TokenOperatorRightShift extends TokenOperator {
    public TokenOperatorRightShift() { super(">>", TokenType.OP_RIGHT_SHIFT); }
}

// Unäre Inkrement/Dekrement
class TokenOperatorIncrement extends TokenOperator {
    public TokenOperatorIncrement() { super("++", TokenType.OP_INCREMENT); }
}

class TokenOperatorDecrement extends TokenOperator {
    public TokenOperatorDecrement() { super("--", TokenType.OP_DECREMENT); }
}

// --- 4. Schlüsselwörter ---

abstract class TokenKw extends TokenSymbol {
    public TokenKw(String value, TokenType type) { super(value, type); }
}

class TokenKwVar extends TokenKw {
    public TokenKwVar() { super("var", TokenType.KW_VAR); }
}

class TokenKwIf extends TokenKw {
    public TokenKwIf() { super("if", TokenType.KW_IF); }
}

class TokenKwElse extends TokenKw {
    public TokenKwElse() { super("else", TokenType.KW_ELSE); }
}

class TokenKwWhile extends TokenKw {
    public TokenKwWhile() { super("while", TokenType.KW_WHILE); }
}

class TokenKwFnDecl extends TokenKw {
    public TokenKwFnDecl() { super("func", TokenType.KW_FUNC); }
}

class TokenKwClassDecl extends TokenKw {
    public TokenKwClassDecl() { super("class", TokenType.KW_CLASS); }
}

class TokenKwReturn extends TokenKw {
    public TokenKwReturn() {super("return", TokenType.KW_RETURN);}
}

class TokenKwFinal extends TokenKw {
    public TokenKwFinal() { super("final", TokenType.KW_FINAL); }
}

class TokenKwNew extends TokenKw {
    public TokenKwNew() { super("new", TokenType.KW_NEW); }
}

class TokenKwTry extends TokenKw {
    public TokenKwTry() { super("try", TokenType.KW_TRY); }
}

class TokenKwCatch extends TokenKw {
    public TokenKwCatch() { super("catch", TokenType.KW_CATCH); }
}

class TokenKwFinally extends TokenKw {
    public TokenKwFinally() { super("finally", TokenType.KW_FINALLY); }
}