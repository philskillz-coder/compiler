package compiler.visitors.eval;

import compiler.ast.*;
import compiler.ast.builtins.Print;
import compiler.ast.builtins.VirtualBlockExpr;
import compiler.visitors.ASTBaseVisitor;
import compiler.visitors.eval.exceptions.EvalException;

public class ASTEvalVisitor extends ASTBaseVisitor<Object> {
    private Environment globalEnv; // current scope
    private Environment env;

    private enum EvalMode {
        REFERENCE,
        VALUE
    }

    private EvalMode nextMode = EvalMode.VALUE;

    private void setEvalMode(EvalMode mode) {
        this.nextMode = mode;
    }

    private void resetEvalMode() {
        this.nextMode = EvalMode.VALUE;
    }

    public ASTEvalVisitor() {
        this.globalEnv = new Environment();
        this.globalEnv.defineFunction(
            "print",
                new Print()
        );
        this.env = this.globalEnv;
    }

    @Override
    public Object visitProgramNode(ProgramNode node) {
        for (Stmt stmt : node.nodes) {
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Object visitLiteralInt(LiteralIntNode node) {
        return node.value;
    }

    @Override
    public Object visitLiteralFloat(LiteralFloatNode node) {
        return node.value;
    }

    @Override
    public Object visitLiteralString(LiteralStringNode node) {
        return node.value;
    }

    @Override
    public Object visitBinaryOp(BinaryOpNode node) {
        Object _lhs = node.lhs.accept(this);
        Object _rhs = node.rhs.accept(this);
        Object result = null; // Variable zur Speicherung des Ergebnisses

        // --- 1. ARITHMETISCHE & BITWEISE OPERATIONEN (Erfordert Integer) ---
        if (node.op.isArithmeticOrBitwise()) {
            if (!(_lhs instanceof Integer) || !(_rhs instanceof Integer)) {
                System.err.println("Type error: expected integers for arithmetic or bitwise operation with operator " + node.op);
                return null;
            }
            int lhs = (int) _lhs;
            int rhs = (int) _rhs;

            switch (node.op) {
                case ADD: result = lhs + rhs; break;
                case SUB: result = lhs - rhs; break;
                case MUL: result = lhs * rhs; break;
                case DIV:
                    if (rhs == 0) {
                        throw new EvalException("Runtime error: Division by zero.");
                    }
                    result = lhs / rhs;
                    break;
                case MOD: result = lhs % rhs; break;
                case BITWISE_AND: result = lhs & rhs; break;
                case BITWISE_OR: result = lhs | rhs; break;
                case BITWISE_XOR: result = lhs ^ rhs; break;
                case LEFT_SHIFT: result = lhs << rhs; break;
                case RIGHT_SHIFT: result = lhs >> rhs; break;
                case POWER: result = (int) Math.pow(lhs, rhs); break;
                default:
                    System.err.println("Unknown arithmetic/bitwise operator: " + node.op);
                    return null;
            }
        }

        // --- 2. LOGISCHE OPERATIONEN (Erfordert Boolean) ---
        else if (node.op.isLogical()) {
            if (!(_lhs instanceof Boolean) || !(_rhs instanceof Boolean)) {
                System.err.println("Type error: expected booleans for logical operation with operator " + node.op);
                return null;
            }
            boolean lhs = (boolean) _lhs;
            boolean rhs = (boolean) _rhs;

            switch (node.op) {
                case LOGICAL_AND: result = lhs && rhs; break;
                case LOGICAL_OR: result = lhs || rhs; break;
                default: return null;
            }
        }

        // --- 3. VERGLEICHSOPERATIONEN (Erfordert Integer oder andere vergleichbare Typen) ---
        else if (node.op.isComparison()) {
            if (!(_lhs instanceof Integer) || !(_rhs instanceof Integer)) {
                System.err.println("Type error: expected comparable types (e.g., Integer) for comparison operation " + node.op);
                return null;
            }
            int lhs = (int) _lhs;
            int rhs = (int) _rhs;

            switch (node.op) {
                case EQUAL: result = lhs == rhs; break;
                case NOT_EQUAL: result = lhs != rhs; break;
                case LESS: result = lhs < rhs; break;
                case GREATER: result = lhs > rhs; break;
                case LESS_EQUAL: result = lhs <= rhs; break;
                case GREATER_EQUAL: result = lhs >= rhs; break;
                default: return null;
            }
        }

        // Wenn der Operator nicht behandelt wurde
        if (result == null) {
            System.err.println("Unknown or unhandled operator: " + node.op);
            return null;
        }

        return result;
    }

    @Override
    public Object visitUnaryOp(UnaryOpNode node) {
        Object _value = node.value.accept(this);
        Object result = null;

        // --- 1. INKREMENT/DEKREMENT (Muss Variablenzugriff sein) ---
        if (node.op.isIncrementOrDecrement()) {
            if (!(_value instanceof Integer)) {
                System.err.println("Runtime error: Increment/Decrement can only be applied to an integer variable.");
                return null;
            }

            int oldValue = (int) _value;
            int newValue = 0;

            // Berechne den neuen Wert
            switch (node.op) {
                case PRE_INC:
                case POST_INC:
                    newValue = oldValue + 1;
                    break;
                case PRE_DEC:
                case POST_DEC:
                    newValue = oldValue - 1;
                    break;
                default:
                    break;
            }

            // --- Zuweisung des neuen Wertes in der Environment ---
            // Annahme: Sie haben eine Methode 'setVariableValue' und können den Namen ermitteln
            // env.setVariableValue(((VariableAccessNode) node.value).name.name, newValue);

            // Gib den korrekten Wert zurück (Post- vs. Pre-Operator)
            switch (node.op) {
                case PRE_INC:
                case PRE_DEC:
                    result = newValue; // PRE gibt den NEUEN Wert zurück
                    break;
                case POST_INC:
                case POST_DEC:
                    result = oldValue; // POST gibt den ALTEN Wert zurück
                    break;
                default:
                    break;
            }
        }

        // --- 2. STANDARD UNÄRE OPERATIONEN ---
        else {
            switch (node.op) {
                case NEGATE:
                case BITWISE_NOT:
                    if (!(_value instanceof Integer)) {
                        System.err.println("Type error: expected integer for operation " + node.op);
                        return null;
                    }
                    int value = (int) _value;
                    if (node.op == UnaryOperator.NEGATE) {
                        result = -value;
                    } else {
                        result = ~value;
                    }
                    break;

                case LOGIC_NOT:
                    if (_value instanceof Boolean) {
                        result = !((boolean) _value);
                    } else if (_value instanceof Integer) {
                        // C-Stil: 0 ist false, alles andere ist true
                        result = ((int) _value) == 0;
                    } else {
                        System.err.println("Type error: expected boolean or integer for LOGIC_NOT.");
                        return null;
                    }
                    break;

                default:
                    System.err.println("Unknown unary operator: " + node.op);
                    return null;
            }
        }

        return result;
    }

    @Override
    public Object visitIfStmt(IfStmtNode node) {
        Object _condition = node.condition.accept(this);

        if (!(_condition instanceof Boolean)) {
            System.err.println("Type error: if-condition must be boolean, got " +
                    (_condition == null ? "null" : _condition.getClass().getSimpleName()));
            return null;
        }

        boolean condition = (Boolean) _condition;

        if (condition) {
            node.thenStatement.accept(this);
        } else if (node.elseBranch != null) {
            node.elseBranch.accept(this);
        }

        return null; // statements don't return a value
    }

    @Override
    public Object visitIdentifier(IdentifierNode node) {
        String name = node.identifier;

        EvalMode currentMode = this.nextMode;
        resetEvalMode();

        if (currentMode == EvalMode.VALUE) {
            // R-Value: Liefere den Wert
            if (!env.varExists(name)) {
                throw new EvalException("Undefined variable: " + name);
            }
            return env.getVar(name);
        } else {
            // L-Value (Zuweisung): Liefere Namen
            return name;
        }
    }

    @Override
    public Object visitWhileStmt(WhileStmtNode node) {
        while (true) {
            Object _condition = node.condition.accept(this);
            if (!(_condition instanceof Boolean)) {
                System.err.println("Type error: while-condition must be boolean, got " +
                        (_condition == null ? "null" : _condition.getClass().getSimpleName()));
                return null;
            }
            boolean condition = (Boolean) _condition;

            if (condition) {
                node.body.accept(this);
            } else {
                break;
            }
        }

        return null;
    }

    @Override
    public Object visitBlockExpr(BlockExpr node) {
        Environment previous = env; // todo: see if this fits with function also creating env
        env = new Environment(previous); // new local scope

        try {
            for (ASTNode stmt : node.statements) {
                if (stmt instanceof ReturnStmtNode || stmt instanceof ResultStmtNode) {
                    return stmt.accept(this);
                }
                stmt.accept(this);
            }
        } finally {
            env = previous; // restore outer scope
        }

        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt node) {
        if (!(node instanceof VirtualBlockExpr)) {
            for (ASTNode stmt : node.statements) {
                if (stmt instanceof ReturnStmtNode || stmt instanceof ResultStmtNode) {
                    return stmt.accept(this);
                }
                stmt.accept(this);
            }
        } else {
            return ((VirtualBlockExpr) node).execute(env);
        }

        return null;
    }

    @Override
    public Object visitFunctionDecl(FunctionDeclNode node) {
        setEvalMode(EvalMode.REFERENCE);
        String name = (String) node.name.accept(this);

        if (env.assnExists(name)) {
            throw new EvalException("Function or variable named \"" + name + "\" already exists");
        }

        env.defineFunction(name, node);
        // Optional: you could store a snapshot of the current env in node.environment for closures
        // update function environment to reflect function parameters
//        if (node.body instanceof BlockStmt) ((BlockStmt) node.body).environment = env;

        return null;
    }

    @Override
    public Object visitFunctionCall(FunctionCallNode node) {
        setEvalMode(EvalMode.REFERENCE);
        String name = (String) node.callee.accept(this);

        if (!env.functionExists(name)) {
            throw new EvalException("Function named \"" + name + "\" doesn't exist in this context!");
        }

        FunctionDeclNode fn = env.getFunction(name);
        // ensure parameters match
        if (node.parameters.size() != fn.parameters.size()) {
            throw new EvalException("Function \"" + name + "\" expects " + fn.parameters.size() +
                    " parameters, but " + node.parameters.size() + " were provided.");
        }

        Environment fCallEnv = new Environment(env);
        // build the function environment
        for (int i=0; i<node.parameters.size(); i++) {
            VariableDeclNode formal = fn.parameters.get(i); // todo: verify type
            Expr actual = node.parameters.get(i);
            setEvalMode(EvalMode.REFERENCE);

            String formalName = (String) formal.name.accept(this);
            if (fCallEnv.varExists(formalName)) {
                throw new EvalException("Parameter name \"" + formalName + "\" already exists in function scope.");
            }
            fCallEnv.defineVar(formalName, actual.accept(this));
        }
        Environment previous = env;
        env = fCallEnv;

        Object res = fn.body.accept(this);
        env = previous;

        return res;
    }

    @Override
    public Object visitReturn(ReturnStmtNode node) {
        return node.returnValue.accept(this);
    }

    @Override
    public Object visitResult(ResultStmtNode node) {
        return node.resultValue.accept(this);
    }

    @Override
    public Object visitVariableDef(VariableDefNode node) {
        setEvalMode(EvalMode.REFERENCE);
        String name = (String) node.name.accept(this);

        if (env.assnExists(name)) {
            throw new EvalException("Function or Variable named \"" + name + "\" already exists");
        }

        Object value = node.initialValue.accept(this);

        env.defineVar(name, value);

        return null;
    }

    @Override
    public Object visitVariableAssn(VariableAssnNode node) {
        setEvalMode(EvalMode.REFERENCE);
        String name = (String) node.name.accept(this);

        if (!env.varExists(name)) {
            throw new EvalException("Variable named \"" + name + "\" doesn't exist in this context!");
        }

        Object value = node.newValue.accept(this);
        env.assignVar(name, value);

        return value;
    }

    @Override
    public Object visitVariableDecl(VariableDeclNode node) {
        setEvalMode(EvalMode.REFERENCE);
        String name = (String) node.name.accept(this);
        if (env.assnExists(name)) {
            throw new EvalException("Function or Variable named \"" + name + "\" already exists");
        }

        env.defineVar(name, null);

        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmtNode node) {
        node.expr.accept(this);
        return null;
    }

}
