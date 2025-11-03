package compiler.parser;

import compiler.ast.*;
import compiler.ast.ExprStmtNode;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TreeBuilder {
    private int current = 0;
    private final List<Token<?>> tokens;

    public TreeBuilder(List<Token<?>> tokens) {
        this.tokens = tokens;
    }

    public ProgramNode parse() {
        consume(TokenType.SOF);
        List<Stmt> declarations = new ArrayList<>();

        // Parsen Sie alle Deklarationen und globalen Anweisungen, bis das Ende erreicht ist
        while (!isAtEnd()) {
            declarations.add(parseDeclaration());
        }

        // Annahme: ProgramNode ist Ihr AST-Wurzelknoten
        return new ProgramNode(declarations);
    }

    private Stmt parseDeclaration() {
        // 1. Funktion (func)
        if (match(TokenType.KW_FUNC)) {
            return parseFunctionDeclaration(); // Gibt FunctionDeclNode zurück
        }
        if (match(TokenType.KW_VAR)) {
            return parseVariableDeclaration();
        }

        // Wenn es keine Deklaration ist, muss es eine Anweisung sein,
        return parseStatement();
    }

    private Stmt parseStatement() {
        if (match(TokenType.KW_IF)) return parseIfStatement();
        if (match(TokenType.KW_WHILE)) return parseWhileStatement();
        if (match(TokenType.KW_RETURN)) return parseReturnStatement();
        if (match(TokenType.CURLY_OPEN)) return parseBlockStatement();

        // Falls es kein Statement ist, behandle es als Anweisung
        return parseExpressionStatement();
    }

    private ExprStmtNode parseExpressionStatement() {
        Expr expr = parseExpression();
        consume(TokenType.SEMICOLON);

        return new ExprStmtNode(expr);
    }

    private BlockStmt parseBlockStatement() {
        List<Stmt> nodes = new ArrayList<>();

        while (!peekMatch(TokenType.CURLY_CLOSE) && !isAtEnd()) {
            nodes.add(parseDeclaration());
        }

        consume(TokenType.CURLY_CLOSE);

        return new BlockStmt(nodes);
    }

    private FunctionDeclNode parseFunctionDeclaration() {
        // 1. Funktionsname
        Token<?> nameToken = consume(TokenType.IDENTIFIER);

        // 2. Parameter-Liste
        consume(TokenType.PAREN_OPEN);
        List<VariableDeclNode> parameters = new ArrayList<>();

        while (!peekMatch(TokenType.PAREN_CLOSE) && !isAtEnd()) {
            VariableDeclNode par = parseVariableDeclaration();
            parameters.add(par);

            if (!peekMatch(TokenType.PAREN_CLOSE)) {
                consume(TokenType.COMMA);
            }
        }

        consume(TokenType.PAREN_CLOSE);
        // 3. Rückgabetyp (optional, hier ausgelassen)
        // todo

        // 4. Funktionskörper (Body)
        consume(TokenType.CURLY_OPEN);
        BlockStmt body = parseBlockStatement();

        // Annahme: Sie haben einen Konstruktor, der Name, Parameter und Body annimmt
        return new FunctionDeclNode(new IdentifierNode(nameToken.getValue().toString()), parameters, body, new IdentifierNode("void"));
    }

    private VariableDeclNode parseVariableDeclaration() {
        String type = "any"; // todo: ???
        Token<?> nameToken = consume(TokenType.IDENTIFIER);
        String name = nameToken.getValue().toString();

        if (match(TokenType.OP_ASSIGN)) { // definition
            Expr initialValue = parseExpression();
            consume(TokenType.SEMICOLON);

            return new VariableDefNode(new IdentifierNode(type), new IdentifierNode(name), initialValue);
        } else { // declaration
            consume(TokenType.SEMICOLON);
            return new VariableDeclNode(new IdentifierNode(type), new IdentifierNode(name));
        }
    }

    private IfStmtNode parseIfStatement() {
        // 1. Bedingung parsen: 'if' (Expression)
        consume(TokenType.PAREN_OPEN);
        Expr condition = parseExpression();
        consume(TokenType.PAREN_CLOSE);

        // 2. Den "Then"-Körper parsen (kann Block oder Einzel-Statement sein)
        Stmt thenBranch = parseStatement();
        Stmt elseBranch = null;

        if (peekMatch(TokenType.KW_ELSE)) {
            consume(TokenType.KW_ELSE);

            // Prüfen auf 'else if'
            if (peekMatch(TokenType.KW_IF)) {
                consume(TokenType.KW_IF); // Das 'if' des 'else if' konsumieren

                // REKURSION: Der elseBranch ist der nächste IfStmtNode
                elseBranch = parseIfStatement();

            } else {
                // Standard 'else' Body
                elseBranch = parseStatement();
            }
        }

        // Die gesamte Struktur wird in einem einzigen, rekursiven Knoten zurückgegeben.
        return new IfStmtNode(condition, thenBranch, elseBranch);
    }

    private WhileStmtNode parseWhileStatement() {
        consume(TokenType.PAREN_OPEN);
        Expr cond = parseExpression();
        consume(TokenType.PAREN_CLOSE);
        Stmt body = parseStatement();

        return new WhileStmtNode(cond, body);
    }

    private ReturnStmtNode parseReturnStatement() {
        Expr expr;
        if (peek().getType() == TokenType.SEMICOLON) {
            expr = null;
        } else {
            expr = parseExpression();
        }

        consume(TokenType.SEMICOLON);

        return new ReturnStmtNode(expr);
    }

    /*
    Präzedenz:
    1. Zuweisung: = (Rechts-assoziativ)
    2. Logisches ODER: ||
    3. Logisches UND: &&
    4. Bitweises ODER: |
    5. Bitweises XOR: ^
    6. Bitweises UND: &
    7. Gleichheit: ==, !=
    8. Vergleich: <, <=, >, >=
    9. Shift-Operationen: <<, >>
    10. Term: +, -
    11. Faktor: *, /, %
    12. Potenz/Exponent: ** (Rechts-assoziativ)
    13. Unär/Prefix: ++ (prä), -- (prä), - (Negation), ! (Logisches NICHT), ~ (Bitweises NICHT) (Rechts-assoziativ)
    14. Suffix/Call/Zugriff: Funktionsaufruf (), Property-Zugriff ., ++ (post), -- (post) (Links-assoziativ)
    15. Primär: Zahlen, Strings, Variablen, Geklammerte Ausdrücke (expr)
    */

    private Expr parseBinaryLeft(Supplier<Expr> nextLevel, TokenType... tokenTypes) {
        Expr lhs = nextLevel.get();

        while (match(tokenTypes) && !isAtEnd()) {
            Token<?> op = tokens.get(current-1);
            Expr rhs = nextLevel.get();
            lhs = new BinaryOpNode(
                    mapTokenToBinaryOp(op.getType()),
                    lhs, rhs
            );
        }

        return lhs;
    }

    private Expr parseBinaryRight(Supplier<Expr> nextLevel, TokenType... tokenTypes) {
        Expr lhs = nextLevel.get();

        if (match(tokenTypes)) {
            Token<?> op = tokens.get(current-1);
            Expr rhs = parseBinaryRight(nextLevel, tokenTypes);
            return new BinaryOpNode(mapTokenToBinaryOp(op.getType()), lhs, rhs);
        }

        return lhs;
    }

    // Der Einstiegspunkt
    private Expr parseExpression() {
        return parseAssignment();
    }

    // 1. Zuweisung (Rechts-assoziativ)
    private Expr parseAssignment() {
        Expr lhs = parseLogicalOr();

        if (match(TokenType.OP_ASSIGN)) {
            if (!(lhs instanceof IdentifierNode)) {
                System.err.println("Ungültiges Zuweisungsziel.");
                return null;
            }

            Expr rhs = parseAssignment(); // rekursion wegen rechts-assoziativität
            IdentifierNode target = (IdentifierNode) lhs;
            return new VariableAssnNode(target, rhs);
        }

        return lhs;
    }

    // 2. Logisches ODER (Links-assoziativ)
    private Expr parseLogicalOr() {
        return parseBinaryLeft(
                this::parseLogicalAnd,
                TokenType.OP_LOGICAL_OR
        );
    }

    // 3. Logisches UND (Links-assoziativ)
    private Expr parseLogicalAnd() {
        return parseBinaryLeft(
                this::parseBitwiseOr,
                TokenType.OP_LOGICAL_AND
        );
    }

    // 4. Bitweises ODER: |
    private Expr parseBitwiseOr() {
        return parseBinaryLeft(
                this::parseBitwiseXor,
                TokenType.OP_BITWISE_OR
        );
    }

    // 5. Bitweises XOR: ^
    private Expr parseBitwiseXor() {
        return parseBinaryLeft(
                this::parseBitwiseAnd,
                TokenType.OP_BITWISE_XOR
        );
    }

    // 6. Bitweises UND: &
    private Expr parseBitwiseAnd() {
        return parseBinaryLeft(
                this::parseEquality,
                TokenType.OP_BITWISE_AND
        );
    }

    // 7. Gleichheit: ==, !=
    private Expr parseEquality() {
        return parseBinaryLeft(
                this::parseComparison,
                TokenType.OP_EQUALS, TokenType.OP_NOTEQUAL
        );
    }

    // 8. Vergleich: <, <=, >, >=
    private Expr parseComparison() {
        return parseBinaryLeft(
                this::parseShift,
                TokenType.OP_LESS_THAN, TokenType.OP_LESS_EQUAL,
                TokenType.OP_GREATER_THAN, TokenType.OP_GREATER_EQUAL
        );
    }

    // 9. Shift: <<, >>
    private Expr parseShift() {
        return parseBinaryLeft(
                this::parseTerm,
                TokenType.OP_LEFT_SHIFT, TokenType.OP_RIGHT_SHIFT
        );
    }

    // 10. Term: +,-
    private Expr parseTerm() {
        return parseBinaryLeft(
                this::parseFactor,
                TokenType.OP_ADD, TokenType.OP_SUBTRACT
        );
    }

    // 11. Faktor: *, /, %
    private Expr parseFactor() {
        return parseBinaryLeft(
                this::parseExponent,
                TokenType.OP_MULTIPLY, TokenType.OP_DIVIDE, TokenType.OP_MODULO
        );
    }

    // 12. Potenz/Exponent: ** (Rechts-assoziativ)
    private Expr parseExponent() {
        return parseBinaryRight(
                this::parseUnary,
                TokenType.OP_POWER
        );
    }

    // 13. Unär/Prefix: ++(pre), --(pre), -, !, ~ (Rechts-assoziativ)
    private Expr parseUnary() {
        if (match(TokenType.OP_SUBTRACT,
                TokenType.OP_LOGICAL_NOT,
                TokenType.OP_BITWISE_NOT,
                TokenType.OP_INCREMENT,
                TokenType.OP_DECREMENT)) {

            // Das gefundene Token ist der Operator.
            Token<?> operatorToken = tokens.get(current - 1);

            // REKURSION: Parst den Operanden, der selbst ein weiterer unärer Ausdruck sein könnte
            // Beispiel: !!x wird zu !(!x)
            Expr operand = parseUnary();

            // Mappen Sie das Token auf das UnaryOperator-Enum (analog zu mapTokenToBinaryOp)
            UnaryOperator opEnum = mapTokenToUnaryOp(operatorToken.getType());

            // Erstellt den UnaryOpNode (zwei Felder: Operator, Operand)
            return new UnaryOpNode(opEnum, operand);
        }

        // Wenn kein unärer Operator gefunden wurde, gehe zur nächsthöheren Stufe
        return parseCall();
    }

    // 14. Suffix/Call/Property: (post)++, (post)--, (), . (fn)
    private Expr parseCall() {
        Expr value = parsePrimary();

        while (true) {
            if (match(TokenType.PAREN_OPEN)) {
                value = parseCallSuffix(value);
            } else if (match(TokenType.OP_INCREMENT, TokenType.OP_DECREMENT)) { // post inc/dec
                UnaryOperator op = mapTokenToUnaryOp(tokens.get(current-1).getType());
                value = new UnaryOpNode(op, value);
            } else {
                break;
            }
            break;
        }

        return value;
    }

    // 14a. Call Suffix
    private Expr parseCallSuffix(Expr callee) {
        List<Expr> arguments = new ArrayList<>();

        // 1. Prüfe, ob die Argumentenliste leer ist (case: 'func()')
        // Wenn das nächste Token NICHT die schließende Klammer ist, sind Argumente vorhanden.
        if (!peekMatch(TokenType.PAREN_CLOSE)) {

            // 2. Parsen der Argumente (mindestens ein Argument wird erwartet, falls die Liste nicht leer ist)
            do {
                // Füge den Ausdruck als Argument hinzu
                arguments.add(parseExpression());

                // Prüfe auf das Komma, um festzustellen, ob weitere Argumente folgen
                // Wenn KEIN Komma vorhanden ist, ist die Argumentenliste fertig
                if (!peekMatch(TokenType.COMMA)) {
                    break;
                }

                // Komma gefunden: Konsumiere das Komma, um das nächste Argument zu parsen
                consume(TokenType.COMMA);

                // Fehlerbehandlung: Das letzte Argument darf NICHT von einem Komma gefolgt werden.
                // (Hier könnte eine zusätzliche Prüfung auf "trailing comma" eingefügt werden,
                // je nach den Regeln Ihrer Sprache.)

            } while (true);
        }

        // 3. Konsumiere die schließende Klammer ')'
        // Dies wirft einen Fehler, falls ')' fehlt.
        consume(TokenType.PAREN_CLOSE);

        // Erstelle den CallNode
        return new FunctionCallNode(callee, arguments);
    }

    // 11. Primär: Literal, Variablen, Klammern
    private Expr parsePrimary() {


        // --- LITERALE ---

        if (match(TokenType.INT_LITERAL)) {
            // Holen des generischen Tokens, um auf den Wert zuzugreifen
            Token<?> token = tokens.get(current - 1);
            // Casten des von getValue() zurückgegebenen Object auf den erwarteten Typ (Integer/int)
            return new LiteralIntNode((Integer) token.getValue());
        }

        if (match(TokenType.FLOAT_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            // Casten auf Double
            return new LiteralFloatNode((Double) token.getValue());
        }

        if (match(TokenType.STRING_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            // Casten auf String
            return new LiteralStringNode((String) token.getValue());
        }

        // --- BEZEICHNER ---

        if (match(TokenType.IDENTIFIER)) {
            Token<?> token = tokens.get(current - 1);
            // Casten auf String (Bezeichner-Name)
            return new IdentifierNode((String) token.getValue());
        }

        // --- GEKLAMMERTE AUSDRÜCKE ---

        if (match(TokenType.PAREN_OPEN)) {
            Expr expr = parseExpression();
            consume(TokenType.PAREN_CLOSE);
            return expr;
        }

        // --- SCHLÜSSELWÖRTER (Booleans/Null, falls implementiert) ---
        // ...

        throw new ParseException("Unerwartetes Token:" + "(current=" + current + ")" + " Erwartete ein Literal, Bezeichner oder '('.: " + peek().toString());
    }

    // --- Token-Management Hilfsfunktionen ---

    private Token<?> peek() {
        if (isAtEnd()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(current);
    }

    private boolean peekMatch(TokenType... types) {
        if (isAtEnd()) {
            return false;
        }

        TokenType currentType = peek().getType();

        for (TokenType type : types) {
            if (currentType == type) {
                return true;
            }
        }
        return false;
    }

    private Token<?> consume(TokenType expectedType) {
        if (peek().getType() != expectedType) {
            throw new UnexpectedTypeException("Erwartete Token-Typ " + expectedType + "bei " + current +
                    ", aber fand " + peek().getType());
        }
        return tokens.get(current++); // Liefert das Token und inkrementiert den Zeiger
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (peek().getType() == type) {
                current++;
                return true;
            }
        }
        return false;
    }

    private boolean matchMany(TokenType... types) {
        int savedCurrent = current;

        for (TokenType type : types) {
            if (peek().getType() == type) {
                current++;
            } else {
                current = savedCurrent;
                return false;
            }
        }

        return true;
    }

    private boolean isAtEnd() {
        if (current >= tokens.size()) {
            return true;
        }

        return tokens.get(current).getType() == TokenType.EOF;
    }

    private BinaryOperator mapTokenToBinaryOp(TokenType type) {
        switch (type) {
            // Logische Operatoren
            case OP_LOGICAL_OR:
                return BinaryOperator.LOGICAL_OR;
            case OP_LOGICAL_AND:
                return BinaryOperator.LOGICAL_AND;

            // Gleichheit und Vergleich
            case OP_EQUALS:
                return BinaryOperator.EQUAL;
            case OP_NOTEQUAL:
                return BinaryOperator.NOT_EQUAL;
            case OP_LESS_THAN:
                return BinaryOperator.LESS;
            case OP_GREATER_THAN:
                return BinaryOperator.GREATER;
            case OP_LESS_EQUAL:
                return BinaryOperator.LESS_EQUAL;
            case OP_GREATER_EQUAL:
                return BinaryOperator.GREATER_EQUAL;

            // Arithmetische Operatoren
            case OP_ADD:
                return BinaryOperator.ADD;
            case OP_SUBTRACT:
                return BinaryOperator.SUB;
            case OP_MULTIPLY:
                return BinaryOperator.MUL;
            case OP_DIVIDE:
                return BinaryOperator.DIV;
            case OP_MODULO:
                return BinaryOperator.MOD;

            // Potenz/Exponent
            case OP_POWER:
                return BinaryOperator.POWER;

            // Bitweise Operatoren
            case OP_BITWISE_AND:
                return BinaryOperator.BITWISE_AND;
            case OP_BITWISE_OR:
                return BinaryOperator.BITWISE_OR;
            case OP_BITWISE_XOR:
                return BinaryOperator.BITWISE_XOR;
            case OP_LEFT_SHIFT:
                return BinaryOperator.LEFT_SHIFT;
            case OP_RIGHT_SHIFT:
                return BinaryOperator.RIGHT_SHIFT;

            // Standardfall
            default:
                throw new IllegalArgumentException("Der TokenType " + type + " kann nicht auf einen BinaryOperator abgebildet werden.");
        }
    }

    private UnaryOperator mapTokenToUnaryOp(TokenType type) {
        switch (type) {
            // Arithmetische und Logische Unäroperatoren
            case OP_SUBTRACT:
                // Das '-' Zeichen wird hier als unäre Negation interpretiert
                return UnaryOperator.NEGATE;
            case OP_LOGICAL_NOT:
                return UnaryOperator.LOGIC_NOT;
            case OP_BITWISE_NOT:
                return UnaryOperator.BITWISE_NOT;

            // Präfix Inkrement/Dekrement
            case OP_INCREMENT:
                return UnaryOperator.PRE_INC;
            case OP_DECREMENT:
                return UnaryOperator.PRE_DEC;

            // Standardfall: Werfen einer Ausnahme für nicht unterstützte Typen
            default:
                throw new IllegalArgumentException("Der TokenType " + type + " kann nicht auf einen UnaryOperator abgebildet werden.");
        }
    }
}
