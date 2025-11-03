package compiler.visitors;

import compiler.ast.*;
import compiler.ast.builtins.Print;
import compiler.ast.builtins.VirtualBlock;

public class ASTEvalVisitor extends ASTBaseVisitor<Object> {
    private Environment globalEnv; // current scope
    private Environment env;


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
    public Object visitIntLiteral(LiteralIntNode node) {
        return node.value;
    }

    @Override
    public Object visitFloatLiteral(LiteralFloatNode node) {
        return node.value;
    }

    @Override
    public Object visitStringLiteral(LiteralStringNode node) {
        return node.value;
    }

    @Override
    public Object visitBinaryOp(BinaryOpNode node) {
        Object _lhs = node.lhs.accept(this);
        Object _rhs = node.rhs.accept(this);

        // Ensure both sides are integers
        if (!(_lhs instanceof Integer) || !(_rhs instanceof Integer)) {
            System.err.println("Type error: expected integers for arithmetic operation.");
            return null;
        }

        int lhs = (int) _lhs;
        int rhs = (int) _rhs;

        switch (node.op) {
            case ADD:
                return lhs + rhs;
            case SUB:
                return lhs - rhs;
            case MUL:
                return lhs * rhs;
            case DIV:
                if (rhs == 0) {
                    System.err.println("Runtime error: division by zero.");
                    return null;
                }
                return lhs / rhs;
            default:
                System.err.println("Unknown operator: " + node.op);
                return null;
        }
    }

    @Override
    public Object visitUnaryOp(UnaryOpNode node) {
        Object _value = node.value.accept(this);

        // Ensure both sides are integers
        if (!(_value instanceof Integer)) {
            System.err.println("Type error: expected integers for arithmetic operation.");
            return null;
        }

        int value = (int) _value;

        switch (node.op) {
            case NEGATE:
                return -value;
            case LOGIC_NOT:
                return value != 0; // todo: this only works for int
            case BITWISE_NOT:
            case POST_INC:
            case PRE_INC:
                return 0; // todo
            default:
                System.err.println("Unknown operator: " + node.op);
                return null;
        }
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
    public Object visitIdentifierNode(IdentifierNode node) {
        return node.identifier;
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
    public Object visitBlockStmt(BlockStmt node) {
        Environment previous = env; // todo: see if this fits with function also creating env
        env = new Environment(previous); // new local scope

        if (!(node instanceof VirtualBlock)) {
            try {
                for (ASTNode stmt : node.statements) {
                    if (stmt instanceof ReturnStmtNode) {
                        return stmt.accept(this);
                    }
                    stmt.accept(this);
                }
            } finally {
                env = previous; // restore outer scope
            }
        } else {
            return ((VirtualBlock) node).execute(env);
        }

        return null;
    }


    @Override
    public Object visitFunctionDecl(FunctionDeclNode node) {
        String name = (String) node.name.accept(this);

        if (env.assnExists(name)) {
            System.err.println("Function or Variable named \"" + name + "\" already exists");
            return null;
        }

        env.defineFunction(name, node);
        // Optional: you could store a snapshot of the current env in node.environment for closures
        // update function environment to reflect function parameters
        node.body.environment = env;

        return null;
    }

    @Override
    public Object visitFunctionCall(FunctionCallNode node) {
        String name = (String) node.callee.accept(this);

        if (!env.functionExists(name)) {
            System.err.println("Unknown function reference: <" + name + ">");
            return null;
        }

        FunctionDeclNode fn = env.getFunction(name);
        // ensure parameters match
        if (node.parameters.size() != fn.parameters.size()) {
            System.err.println("Parameter count does not match required parameters");
            return null;
        }


        Environment fCallEnv = new Environment(env);
        // build the function environment
        for (int i=0; i<node.parameters.size(); i++) {
            VariableDeclNode formal = fn.parameters.get(i); // todo: verify type
            Expr actual = node.parameters.get(i);

            fCallEnv.defineVar((String) formal.name.accept(this), actual.accept(this));
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
    public Object visitVariableDef(VariableDefNode node) {
        String name = (String) node.name.accept(this);

        if (env.assnExists(name)) {
            System.err.println("Function or Variable named \"" + name + "\" already exists");
            return null;
        }

        Object value = node.initialValue.accept(this);

        env.defineVar(name, value);

        return null;
    }

    @Override
    public Object visitVariableAssn(VariableAssnNode node) {
        String name = (String) node.name.accept(this);

        if (!env.varExists(name)) {
            System.err.println("Variable named \"" + name + "\" doesn't exist in this context!");
            return null;
        }

        Object value = node.newValue.accept(this);
        env.assignVar(name, value);

        return value;
    }

    @Override
    public Object visitVariableDecl(VariableDeclNode node) {
        String name = (String) node.name.accept(this);
        if (env.assnExists(name)) {
            System.err.println("Function or Variable named \"" + name + "\" already exists");
            return null;
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
