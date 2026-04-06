package compiler.parser;

import compiler.parser.ast.*;
import compiler.parser.ast.ExprStmt;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TreeBuilder {
    private int current = 0;
    private final List<Token<?>> tokens;

    public TreeBuilder(List<Token<?>> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {
        consume(TokenType.SOF);
        List<Stmt> declarations = new ArrayList<>();

        while (!isAtEnd()) {
            declarations.add(parseDeclaration());
        }

        return new Program(declarations);
    }

    /*private Stmt parseDeclaration() {
        if (match(TokenType.KW_CLASS)) {
            return parseClassDeclaration();
        }
        if (match(TokenType.KW_FUNC)) {
            return parseFunctionDeclaration();
        }
        if (match(TokenType.KW_VAR)) {
            return parseVariableDeclaration();
        }

        // Wenn es keine Deklaration ist, muss es eine Anweisung sein,
        return parseStatement();
    }*/
    private Stmt parseDeclaration() {
        if (peekMatch(TokenType.KW_CLASS)) { // Nur gucken!
            return parseClassDeclaration();
        }
        if (peekMatch(TokenType.KW_FUNC)) { // Nur gucken!
            return parseFunctionDeclaration();
        }
        if (peekMatch(TokenType.KW_VAR)) { // Nur gucken!
            return parseVariableDeclaration();
        }
        return parseStatement();
    }

    private Stmt parseStatement() {
        if (match(TokenType.KW_IF)) return parseIfStatement();
        if (match(TokenType.KW_WHILE)) return parseWhileStatement();
        if (match(TokenType.KW_RETURN)) return parseReturnStatement();
        if (match(TokenType.KW_YIELD)) return parseYieldStatement();
        if (match(TokenType.KW_BREAK)) return parseBreakStatement();
        if (match(TokenType.KW_CONTINUE)) return parseContinueStatement();

        // Falls es kein Statement ist, behandle es als Anweisung
        return parseExpressionStatement();
    }

    private Stmt parseBody() {
        if (match(TokenType.CURLY_OPEN)) {
            return parseBlockStatement();
        }

        return parseStatement();
    }

    private ExprStmt parseExpressionStatement() {
        Expr expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected semicolon after expression.");

        return new ExprStmt(expr);
    }

    private Block parseBlockStatement() {
        List<Stmt> nodes = new ArrayList<>();

        while (!peekMatch(TokenType.CURLY_CLOSE) && !isAtEnd()) {
            nodes.add(parseDeclaration());
        }

        consume(TokenType.CURLY_CLOSE, "Expected closing bracket for block statement.");

        return new Block(nodes);
    }

    private YieldBlock parseYieldBlock() {
        List<Stmt> nodes = new ArrayList<>();

        while (!peekMatch(TokenType.CURLY_CLOSE) && !isAtEnd()) {
            nodes.add(parseDeclaration());
        }

        consume(TokenType.CURLY_CLOSE, "Expected closing bracket for block statement.");

        return new YieldBlock(nodes);
    }

    /* private ClassDecl parseClassDeclaration() {
        Token<?> nameToken = consume(TokenType.IDENTIFIER);
        String name = nameToken.getValue().toString();

        consume(TokenType.CURLY_OPEN, "Expected '{' for class body.");

        List<VariableDecl> fields = new ArrayList<>();
        List<FunctionDecl> methods = new ArrayList<>();
        List<ClassDecl> innerClasses = new ArrayList<>();

        while (!peekMatch(TokenType.CURLY_CLOSE) && !isAtEnd()) {
            if (match(TokenType.KW_VAR)) {
                fields.add((VariableDecl) parseVariableDeclaration());
            } else if (match(TokenType.KW_FUNC)) {
                methods.add(parseFunctionDeclaration());
            } else if (match(TokenType.KW_CLASS)) {
                innerClasses.add(parseClassDeclaration());
            } else {
                throw new ParseException("Unexpected token in class body: " + peek());
            }
        }

        consume(TokenType.CURLY_CLOSE, "Expected '}' after class body.");

        return new ClassDecl(name, fields, methods, innerClasses);
    }*/
    private ClassDecl parseClassDeclaration() {
        consume(TokenType.KW_CLASS); // <--- NEU: 'class' wegwerfen
        Set<Modifier> modifiers = parseModifiers();
        Token<?> nameToken = consume(TokenType.IDENTIFIER);
        String name = nameToken.getValue().toString();

        consume(TokenType.CURLY_OPEN, "Expected '{' for class body.");
        List<VariableDecl> fields = new ArrayList<>();
        List<FunctionDecl> methods = new ArrayList<>();
        List<ClassDecl> innerClasses = new ArrayList<>();

        while (!peekMatch(TokenType.CURLY_CLOSE) && !isAtEnd()) {
            if (peekMatch(TokenType.KW_VAR)) { // <--- GEÄNDERT: peekMatch
                fields.add(parseVariableDeclaration());
            } else if (peekMatch(TokenType.KW_FUNC)) { // <--- GEÄNDERT: peekMatch
                methods.add(parseFunctionDeclaration());
            } else if (peekMatch(TokenType.KW_CLASS)) { // <--- GEÄNDERT: peekMatch
                innerClasses.add(parseClassDeclaration());
            } else {
                throw new ParseException("Unexpected token in class body: " + peek());
            }
        }
        consume(TokenType.CURLY_CLOSE, "Expected '}' after class body.");
        ClassDecl c = new ClassDecl(modifiers, name, fields, methods, innerClasses);
        return c;
    }


    private FunctionDecl parseFunctionDeclaration() {
        consume(TokenType.KW_FUNC); // <--- NEU: 'func' wegwerfen
        Set<Modifier> modifiers = parseModifiers();
        Token<?> returnTypeToken = consume(TokenType.IDENTIFIER);
        Type returnType = new Type(returnTypeToken.getValue().toString());

        Token<?> nameToken = consume(TokenType.IDENTIFIER);
        String name = nameToken.getValue().toString();

        consume(TokenType.PAREN_OPEN, "Expected '(' after function name.");
        List<VariableDecl> parameters = new ArrayList<>();
        while (!peekMatch(TokenType.PAREN_CLOSE) && !isAtEnd()) {
            parameters.add(parseVariableCore());
            if (!peekMatch(TokenType.PAREN_CLOSE)) {
                consume(TokenType.COMMA, "Expected ',' between parameters.");
            }
        }
        consume(TokenType.PAREN_CLOSE);

        Block body = (Block) parseBody();

        return new FunctionDecl(modifiers, returnType, name, parameters, body);
    }

    // Parst nur den Typ und Namen einer Variablen (oder eines Parameters)
    private VariableDecl parseVariableCore() {
        // 1. Modifier einsammeln (können auch leer sein)
        Set<Modifier> modifiers = parseModifiers();

        // 2. Typ parsen
        Token<?> typeToken = consume(TokenType.IDENTIFIER, "Typ erwartet.");
        Type type = new Type(typeToken.getValue().toString());

        // 3. Name parsen
        Token<?> nameToken = consume(TokenType.IDENTIFIER, "Variablenname erwartet.");
        String name = nameToken.getValue().toString();

        return new VariableDecl(type, name, modifiers);
    }

    private VariableDecl parseVariableDeclaration() {
        consume(TokenType.KW_VAR);
        VariableDecl decl = parseVariableCore(); // type name

        if (match(TokenType.OP_ASSIGN)) {
            Expr initialValue = parseExpression();
            consume(TokenType.SEMICOLON, "Erwarte ';' nach der Variablendefinition.");
            return new VariableDef(decl.type, decl.name, initialValue, decl.modifiers);
        } else {
            consume(TokenType.SEMICOLON, "Erwarte ';' nach der Variablendeklaration.");
            return decl;
        }
    }

    private IfStmt parseIfStatement() {
        // 1. Bedingung parsen: 'if' (Expression)
        consume(TokenType.PAREN_OPEN);
        Expr condition = parseExpression();
        consume(TokenType.PAREN_CLOSE);

        // 2. Den "Then"-Körper parsen (kann Block oder Einzel-Statement sein)
        Stmt thenBranch = parseBody();
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
                elseBranch = parseBody();
            }
        }

        // Die gesamte Struktur wird in einem einzigen, rekursiven Knoten zurückgegeben.
        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private WhileStmt parseWhileStatement() {
        consume(TokenType.PAREN_OPEN);
        Expr cond = parseExpression();
        consume(TokenType.PAREN_CLOSE);
        Stmt body = parseBody();

        return new WhileStmt(cond, body);
    }

    private ReturnStmt parseReturnStatement() {
        Expr expr = null;
        if (!peekMatch(TokenType.SEMICOLON)) {
            expr = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Erwarte ';' nach dem 'return'-Statement.");

        return new ReturnStmt(expr);
    }

    private YieldStmt parseYieldStatement() {
        Expr expr = null;
        if (!peekMatch(TokenType.SEMICOLON)) {
            expr = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Erwarte ';' nach dem 'yield'-Statement.");

        return new YieldStmt(expr);
    }

    private BreakStmt parseBreakStatement() {
        return new BreakStmt();
    }

    private ContinueStmt parseContinueStatement() {
        return new ContinueStmt();
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

    // ((A+B)+C)
    private Expr parseBinaryLeft(Supplier<Expr> nextLevel, TokenType... tokenTypes) {
        Expr lhs = nextLevel.get();

        while (match(tokenTypes) && !isAtEnd()) {
            Token<?> op = tokens.get(current-1);
            Expr rhs = nextLevel.get();
            lhs = new BinaryOp(
                    mapTokenToBinaryOp(op.getType()),
                    lhs, rhs
            );
        }

        return lhs;
    }

    // (a = (b = 1))
    private Expr parseBinaryRight(Supplier<Expr> nextLevel, TokenType... tokenTypes) {
        Expr lhs = nextLevel.get();

        if (match(tokenTypes)) {
            Token<?> op = tokens.get(current-1);
            Expr rhs = parseBinaryRight(nextLevel, tokenTypes);
            return new BinaryOp(mapTokenToBinaryOp(op.getType()), lhs, rhs);
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
            if (!(lhs instanceof VariableExpr || lhs instanceof FieldAccessExpr)) {
                throw new ParseException("Invalid assignment target.");
            }

            Expr rhs = parseAssignment();
            return new AssignExpr(lhs, rhs);
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
            return new UnaryOp(opEnum, operand);
        }

        // Wenn kein unärer Operator gefunden wurde, gehe zur nächsthöheren Stufe
        return parseCall();
    }

    // 14. Suffix/Call/Property: (post)++, (post)--, (), . (fn)
    /* private Expr parseCall() {
        Expr value = parsePrimary();

        while (true) {
            if (match(TokenType.PAREN_OPEN)) {
                value = parseCallSuffix(value);
            } else if (match(TokenType.OP_INCREMENT, TokenType.OP_DECREMENT)) { // post inc/dec
                UnaryOperator op = mapTokenToUnaryOp(tokens.get(current-1).getType());
                value = new UnaryOp(op, value);
            } else {
                break;
            }
            break;
        }

        return value;
    } */
    // 14. Suffix/Call/Property: (post)++, (post)--, (), .
    private Expr parseCall() {
        Expr value = parsePrimary();

        while (true) {
            if (match(TokenType.PAREN_OPEN)) {
                // Funktionsaufruf: value()
                value = parseCallSuffix(value);
            } else if (match(TokenType.PERIOD)) {
                // Property-Zugriff: value.name
                Token<?> name = consume(TokenType.IDENTIFIER, "Erwarte Identifier nach '.'");
                value = new FieldAccessExpr(value, (String) name.getValue());
            } else if (match(TokenType.OP_INCREMENT, TokenType.OP_DECREMENT)) {
                // Postfix-Inkrement/Dekrement: value++
                UnaryOperator op = mapTokenToUnaryOp(tokens.get(current - 1).getType());
                value = new UnaryOp(op, value);
                // Nach einem Postfix-Operator kann normalerweise nichts mehr kommen (a++.)
                break;
            } else {
                break;
            }
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
        return new FunctionCall(callee, arguments);
    }

    // 15. Primär: Literal, Variablen, Klammern
    private Expr parsePrimary() {
        if (match(TokenType.INT_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            return new LiteralInt((Integer) token.getValue());
        }
        if (match(TokenType.FLOAT_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            return new LiteralFloat(((Double) token.getValue()).floatValue());
        }
        if (match(TokenType.STRING_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            return new LiteralString((String) token.getValue());
        }
        if (match(TokenType.BOOL_LITERAL)) {
            Token<?> token = tokens.get(current - 1);
            return new LiteralBool((Boolean) token.getValue());
        }
        if (match(TokenType.IDENTIFIER)) {
            Token<?> token = tokens.get(current - 1);
            return new VariableExpr((String) token.getValue());
        }
        if (match(TokenType.PAREN_OPEN)) {
            Expr expr = parseExpression();
            consume(TokenType.PAREN_CLOSE, "Expected ')'.");
            return expr;
        }
        if (match(TokenType.CURLY_OPEN)) {
            return parseYieldBlock();
        }

        throw new ParseException("Unexpected token: " + peek());
    }

    // --- Token-Management Hilfsfunktionen ---

    private Set<Modifier> parseModifiers() {
        Set<Modifier> modifiers = new HashSet<>();

        if (match(TokenType.KW_PUBLIC)) {
            modifiers.add(Modifier.PUBLIC);
        }
        if (match(TokenType.KW_PRIVATE)) {
            modifiers.add(Modifier.PRIVATE);
        }
        if (match(TokenType.KW_FINAL)) {
            modifiers.add(Modifier.FINAL);
        }
        if (match(TokenType.KW_STATIC)) {
            modifiers.add(Modifier.STATIC);
        }

        return modifiers;
    }

    /**
     * Gibt den aktuellen Token zurück, ohne den Lesepointer weiterzubewegen.
     * <p>
     * Falls das Ende der Tokenliste erreicht ist, wird das letzte Token
     * (typischerweise das EOF-Token) zurückgegeben. Andernfalls wird das
     * Token an der aktuellen Position geliefert.
     *
     * @return das aktuelle Token oder, falls am Ende, das letzte Token der Liste
     */
    private Token<?> peek() {
        if (isAtEnd()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(current);
    }

    /**
     * Prüft, ob das aktuelle Token einem der angegebenen Typen entspricht,
     * ohne den Lesepointer zu bewegen.
     * <p>
     * Befindet sich der Parser bereits am Ende der Tokenliste, wird {@code false} zurückgegeben.
     *
     * @param types eine Liste möglicher Token-Typen
     * @return {@code true}, wenn das aktuelle Token einem der übergebenen Typen entspricht,
     *         ansonsten {@code false}
     */
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

    /**
     * Liest das aktuelle Token, wenn es dem erwarteten Typ entspricht, und gibt es zurück.
     * Andernfalls wird eine {@link UnexpectedTypeException} ausgelöst.
     *
     * @param expectedType der erwartete Token-Typ
     * @return das gelesene Token, falls es den Typ erfüllt
     * @throws UnexpectedTypeException wenn das Token nicht dem erwarteten Typ entspricht
     */
    private Token<?> consume(TokenType expectedType) {
        return consume(expectedType, null);
    }

    /**
     * Liest das aktuelle Token, wenn es dem erwarteten Typ entspricht, und gibt es zurück.
     * Andernfalls wird eine {@link UnexpectedTypeException} mit einer optionalen Zusatzmeldung
     * ausgelöst.
     *
     * @param expectedType der erwartete Token-Typ
     * @param errorMessage eine optionale Fehlermeldung zur Kontextualisierung, kann {@code null} sein
     * @return das gelesene Token, falls es den erwarteten Typ hat
     * @throws UnexpectedTypeException wenn das Token nicht dem erwarteten Typ entspricht
     */
    private Token<?> consume(TokenType expectedType, String errorMessage) {
        if (peek().getType() != expectedType) {
            String contextMessage = (errorMessage != null && !errorMessage.isEmpty()) ? errorMessage + " " : "";
            String debugMessage = String.format(
                    "Fehler bei Token %d: %sErwartete %s, fand aber %s.",
                    current,
                    contextMessage,
                    expectedType,
                    peek().getType()
            );

            throw new UnexpectedTypeException(debugMessage);
        }

        return tokens.get(current++);
    }

    /**
     * Prüft, ob das aktuelle Token einem der angegebenen Typen entspricht.
     * Wenn ja, wird der Lesepointer weiterbewegt.
     *
     * @param types die möglichen Token-Typen, mit denen verglichen wird
     * @return {@code true}, wenn das aktuelle Token einem der Typen entspricht, sonst {@code false}
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (peek().getType() == type) {
                current++;
                return true;
            }
        }
        return false;
    }

    /**
     * Versucht, nacheinander mehrere Token-Typen in genau der angegebenen Reihenfolge
     * abzugleichen. Wenn alle Typen übereinstimmen, wird der Lesepointer entsprechend
     * weiterbewegt. Schlägt auch nur einer der Vergleiche fehl, wird der Pointer zurückgesetzt.
     *
     * @param types die zu prüfende Sequenz von Token-Typen
     * @return {@code true}, wenn alle Token in der angegebenen Reihenfolge übereinstimmen,
     *         sonst {@code false}
     */
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

    /**
     * Prüft, ob der Parser das Ende der Tokenliste erreicht hat.
     * <p>
     * Dies ist der Fall, wenn der Lesepointer hinter dem letzten Token liegt
     * oder wenn das aktuelle Token vom Typ {@code EOF} ist.
     *
     * @return {@code true}, wenn das Ende erreicht wurde, sonst {@code false}
     */
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
