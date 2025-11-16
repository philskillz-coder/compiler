package compiler.visitors.eval;

import compiler.ast.*;
import compiler.visitors.ASTBaseVisitor;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.*;

import java.util.List;

public class ASTEvalVisitor extends ASTBaseVisitor<EvalResult> {

    private final Environment globalEnv;
    private Environment env;

    public ASTEvalVisitor() {
        this.globalEnv = new Environment();

        // Built-in functions
        this.globalEnv.defineFunction(
                "print",
                new Print()
        );
        this.env = this.globalEnv; // current environment starts as global
    }

    @Override
    public EvalResult visitProgramNode(ProgramNode node) {
        for (Stmt stmt : node.nodes) {
            stmt.accept(this);
        }

        return EvalResult.value(NullValue.getInstance());
    }

    @Override
    public EvalResult visitLiteralInt(LiteralIntNode node) {
        return EvalResult.value(new IntegerValue(node.value));
    }

    @Override
    public EvalResult visitLiteralFloat(LiteralFloatNode node) {
        return EvalResult.value(new FloatValue(node.value));
    }

    @Override
    public EvalResult visitLiteralString(LiteralStringNode node) {
        return EvalResult.value(new StringValue(node.value));
    }

    @Override
    public EvalResult visitLiteralBool(LiteralBoolNode node) {
        return EvalResult.value(new BooleanValue(node.value));
    }

    @Override
    public EvalResult visitBinaryOp(BinaryOpNode node) {
        EvalResult _lhs = node.lhs.accept(this);

        if (_lhs.isBreaking()) {
            return _lhs;
        }

        EvalResult _rhs = node.rhs.accept(this);
        if (_rhs.isBreaking()) {
            return _rhs;
        }

        AbstractValue lhs = _lhs.unwrapValue();
        AbstractValue rhs = _rhs.unwrapValue();
        AbstractValue resultValue;

        switch (node.op) {
            case ADD:
                resultValue = lhs.add(rhs);
                break;
            case SUB:
                resultValue = lhs.subtract(rhs);
                break;
            case MUL:
                resultValue = lhs.multiply(rhs);
                break;
            case DIV:
                resultValue = lhs.divide(rhs);
                break;
            case MOD:
                resultValue = lhs.modulo(rhs);
                break;
            case POWER:
                resultValue = lhs.power(rhs);
                break;

            // --- Bitweise Operationen ---
            case BITWISE_AND:
                resultValue = lhs.bitwiseAnd(rhs);
                break;
            case BITWISE_OR:
                resultValue = lhs.bitwiseOr(rhs);
                break;
            case BITWISE_XOR:
                resultValue = lhs.bitwiseXor(rhs);
                break;
            case LEFT_SHIFT:
                resultValue = lhs.leftShift(rhs);
                break;
            case RIGHT_SHIFT:
                resultValue = lhs.rightShift(rhs);
                break;

            // --- Logische Operationen ---
            case LOGICAL_AND:
                resultValue = lhs.logicalAnd(rhs);
                break;
            case LOGICAL_OR:
                resultValue = lhs.logicalOr(rhs);
                break;

            // --- Vergleichsoperationen ---
            case EQUAL:
            case NOT_EQUAL:
            case LESS:
            case GREATER:
            case LESS_EQUAL:
            case GREATER_EQUAL:
                resultValue = lhs.compare(node.op, rhs);
                break;

            default:
                throw new EvalException("Unsupported binary operator encountered: " + node.op);
        }

        // 4. Verpacke das finale Value-Ergebnis
        return EvalResult.value(resultValue);
    }

    @Override
    public EvalResult visitUnaryOp(UnaryOpNode node) {
        EvalResult _valueResult = node.value.accept(this);

        if (_valueResult.isBreaking()) {
            return _valueResult;
        }

        AbstractValue value = _valueResult.unwrapValue();
        AbstractValue resultValue;

        if (node.op.isIncrementOrDecrement()) {
            // todo: implement increment/decrement logic
            // Diese Operationen erfordern, dass node.value ein modifizierbarer Knoten (L-Value, z.B. IdentifierNode) ist.
            // Da der Besucher hier nur den R-Value holt, werfen wir eine EvalException.
            // Die korrekte Logik müsste in einem spezialisierten visitor.visitIdentifier(node) erfolgen.
            throw new EvalException("Syntax Error: Increment/Decrement operators (++, --) must operate directly on a variable (L-Value), not a resulting expression.");
        }

        switch (node.op) {
            case NEGATE:
                resultValue = value.negate();
                break;

            case BITWISE_NOT:
                resultValue = value.bitwiseNot();
                break;

            case LOGIC_NOT:
                resultValue = value.logicalNot();
                break;

            default:
                throw new EvalException("Unknown unary operator: " + node.op);
        }

        return EvalResult.value(resultValue);
    }

    @Override
    public EvalResult visitIfStmt(IfStmtNode node) {
        EvalResult conditionResult = node.condition.accept(this);

        if (!conditionResult.is(EvalResult.ResultType.VALUE)) {
            throw new EvalException("If-condition did not evaluate to a value but to " + conditionResult.type);
        }

        AbstractValue conditionValue = conditionResult.unwrapValue();

        if (!(conditionValue instanceof BooleanValue)) {
            throw new EvalException("Type error: If-condition must evaluate to a BooleanValue, but received " + conditionValue.getClass().getSimpleName());
        }

        boolean condition = (boolean) conditionValue.getNativeAbstractValue();
        EvalResult branchResult = null;

        if (condition) {
            branchResult = node.thenStatement.accept(this);
        } else if (node.elseBranch != null) {
            branchResult = node.elseBranch.accept(this);
        }

        if (branchResult != null && branchResult.isBreaking()) {
            return branchResult;
        }

        return EvalResult.normal();
    }

    @Override
    public EvalResult visitIdentifier(IdentifierNode node) {
        AbstractValue value = env.getVar(node.identifier);

        return EvalResult.value(value);
    }

    @Override
    public EvalResult visitWhileStmt(WhileStmtNode node) {
        Environment env = new Environment(this.env, Environment.Visibility.NORMAL);
        this.env = env; // New scope for the while loop

        EvalResult result;

        while (true) {
            EvalResult _condition = node.condition.accept(this);
            if (!_condition.is(EvalResult.ResultType.VALUE)) {
                throw new EvalException("While-condition did not evaluate to a value but to " + _condition.type);
            }
            AbstractValue conditionValue = _condition.unwrapValue();
            if (!(conditionValue.getNativeAbstractValue() instanceof Boolean)) {
                throw new EvalException("Type error: While-condition must evaluate to a BooleanValue, but received " + conditionValue.getNativeAbstractValue().getClass().getSimpleName());
            }

            boolean condition = (Boolean) conditionValue.getNativeAbstractValue();

            if (condition) {
                EvalResult bodyResult = node.body.accept(this);

                if (bodyResult.is(EvalResult.ResultType.BREAK)) {
                    result = EvalResult.normal();
                    break;
                } else if (bodyResult.is(EvalResult.ResultType.CONTINUE)) {
                    throw new EvalException("Continue statement not supported yet in while loops.");
                } else if (bodyResult.is(EvalResult.ResultType.RETURN)) {
                    result = bodyResult;
                    break;
                }
            } else {
                result = EvalResult.normal();
                break;
            }
        }

        this.env = env.parent; // Restore previous environment
        return result;
    }

    @Override
    public EvalResult visitBlockExpr(BlockExpr node) {
        Environment env = new Environment(this.env, Environment.Visibility.NORMAL);
        this.env = env; // New scope for the block expression

        EvalResult result = null;

        for (ASTNode stmt : node.statements) {
            EvalResult stmtResult = stmt.accept(this);
            if (stmtResult.isBreaking()) {
                result = stmtResult;
                break;
            }
        }

        if (result == null) {
            result = EvalResult.value(NullValue.getInstance()); // null because block EXPR must return a value, Normal otherwise
        }

        this.env = env.parent; // Restore previous environment

        return result;
    }

    @Override
    public EvalResult visitBlockStmt(BlockStmt node) {
        Environment env = new Environment(this.env, Environment.Visibility.NORMAL);
        this.env = env; // New scope for the block statement
        EvalResult result = null;

        if (!(node instanceof VirtualBlockExpr)) {
            for (ASTNode stmt : node.statements) {
                EvalResult stmtResult = stmt.accept(this);

                if (stmtResult.isBreaking()) {
                    result = stmtResult;
                    break;
                }
            }
            if (result == null) {
                result = EvalResult.normal();
            }
        } else {
            result = ((VirtualBlockExpr) node).execute(this.env);
        }

        this.env = env.parent; // Restore previous environment
        return result;
    }

    @Override
    public EvalResult visitFunctionDecl(FunctionDeclNode node) {
        String name = node.name.identifier;

        if (env.assnExists(name, false)) {
            throw new EvalException("Function or variable named \"" + name + "\" already exists");
        }

        env.defineFunction(name, node);

        return EvalResult.normal(); // todo: return normal or reference?
    }

    @Override
    public EvalResult visitFunctionCall(FunctionCallNode node) {
        String name;
        if (node.callee instanceof IdentifierNode) {
            name = ((IdentifierNode) node.callee).identifier;
        } else {
            EvalResult calleeResult = node.callee.accept(this);
            if (!calleeResult.is(EvalResult.ResultType.VALUE)) {
                throw new EvalException("Function call callee did not evaluate to a value but to " + calleeResult.type);
            }
            AbstractValue calleeValue = calleeResult.unwrapValue();
            if (!(calleeValue instanceof StringValue)) {
                throw new EvalException("Function call callee must evaluate to a StringValue representing the function name.");
            }
            name = (String) calleeValue.getNativeAbstractValue();
        }

        if (!env.funcExists(name, true)) {
            throw new EvalException("Function named \"" + name + "\" doesn't exist in this context!");
        }

        FunctionDeclNode fnDecl = env.getFunction(name);
        Environment currentEnv = this.env;
//        Environment parent = this.env.findParentEnvironment(); // Finds the first environment that was not jumped to
        Environment parent = globalEnv; // todo: fix - but for now keep global parent scope for functions
        Environment newEnv = new Environment(parent, Environment.Visibility.JUMP);
//        this.env = new Environment(parent, Environment.Visibility.JUMP);

        if (node.parameters.size() != fnDecl.parameters.size()) {
            throw new EvalException("Function \"" + name + "\" expects " + fnDecl.parameters.size() +
                    " parameters, but " + node.parameters.size() + " were provided.");
        }

        // Evaluate and bind parameters while keeping old env for evaluation
        for (int i=0; i<node.parameters.size(); i++) {
            VariableDeclNode formal = fnDecl.parameters.get(i);
            Expr actual = node.parameters.get(i);

            EvalResult actualValue = actual.accept(this);
            if (!actualValue.is(EvalResult.ResultType.VALUE)) {
                throw new EvalException("Function parameter did not evaluate to a value but to " + actualValue.type);
            }

            String formalName = formal.name.identifier;

            if (newEnv.varExists(formalName, false)) {
                throw new EvalException("Parameter name \"" + formalName + "\" already exists in function scope.");
            }

            AbstractValue actualUnwrapped = actualValue.unwrapValue();
            newEnv.defineVar(formalName, actualUnwrapped);
//            System.out.println("Defined parameter \"" + formalName + "\" with value: " + actualUnwrapped.getNativeAbstractValue());
        }

        EvalResult res;
        this.env = newEnv; // Switch to function environment for body execution

        if (fnDecl.isBuiltin) {
            VirtualBlockExpr builtIn = (VirtualBlockExpr) fnDecl.body;

            res = builtIn.execute(this.env);
        }
        else {
            res = fnDecl.body.accept(this);
        }

        this.env = currentEnv; // Restore previous environment

        if (res.is(EvalResult.ResultType.RETURN)) {
            return EvalResult.value(res.unwrapReturnValue()); // terminiere return signal da funktion beendet
        }

        if (res.isBreaking()) {
            throw new EvalException("Unexpected break/continue statement outside of loop in function \"" + name + "\".");
        }

        return EvalResult.value(NullValue.getInstance()); // functions return null if no return statement is executed
    }

    @Override
    public EvalResult visitReturn(ReturnStmtNode node) {
        EvalResult returnValueResult = node.returnValue.accept(this);
        if (returnValueResult.is(EvalResult.ResultType.NORMAL)) {
            throw new EvalException("Return statement must return a value.");
        }

        if (!returnValueResult.is(EvalResult.ResultType.VALUE)) {
            // break/continue/return signal weiterleiten (geht nur wenn: return { break; } etc.)
            return returnValueResult;
        }

        AbstractValue value = returnValueResult.unwrapValue();

        return EvalResult.returnValue(value);
    }

    @Override
    public EvalResult visitResult(ResultStmtNode node) {
        EvalResult resultValueResult = node.resultValue.accept(this);
        if (resultValueResult.is(EvalResult.ResultType.NORMAL)) {
            throw new EvalException("Result statement must return a value.");
        }

        if (!resultValueResult.is(EvalResult.ResultType.VALUE)) {
            // break/continue/return signal weiterleiten (geht nur wenn: result { break; } etc.)
            return resultValueResult;
        }

        AbstractValue value = resultValueResult.unwrapValue();
        return EvalResult.value(value);
    }

    @Override
    public EvalResult visitVariableDef(VariableDefNode node) {
        String name = node.name.identifier;

        if (env.assnExists(name, false)) {
            throw new EvalException("Function or variable named \"" + name + "\" already exists");
        }

        EvalResult value = node.initialValue.accept(this);

        if (value.is(EvalResult.ResultType.NORMAL)) {
            throw new EvalException("Variable initialization must return a value.");
        }

        if (value.isBreaking()) {
            throw new EvalException("Variable initialization cannot contain break/continue/return statements.");
        }

        AbstractValue initValue = value.unwrapValue();
        env.defineVar(name, initValue);

        return value;
    }

    @Override
    public EvalResult visitVariableAssn(VariableAssnNode node) {
        String name = node.name.identifier;

        EvalResult value = node.newValue.accept(this);

        if (value.is(EvalResult.ResultType.NORMAL)) {
            throw new EvalException("Variable assignment must return a value.");
        }
        if (value.isBreaking()) {
            throw new EvalException("Variable assignment cannot contain break/continue/return statements.");
        }
        AbstractValue initValue = value.unwrapValue();
        env.assignVar(name, initValue);

        return value;
    }

    @Override
    public EvalResult visitVariableDecl(VariableDeclNode node) {
        String name = node.name.identifier;
        if (env.assnExists(name, false)) {
            throw new EvalException("Function or Variable named \"" + name + "\" already exists");
        }

        env.defineVar(name, null);

        return EvalResult.normal();
    }

    @Override
    public EvalResult visitExprStmt(ExprStmtNode node) {
        EvalResult res = node.expr.accept(this);
        if (res.isBreaking()) {
            return res;
        }
        return EvalResult.normal();
    }

    private static boolean checkType(Object obj, List<Class<?>> types) {
        for (Class<?> type : types) {
            if (type.isInstance(obj)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkType(Object obj, Class<?>... types) {
        for (Class<?> type : types) {
            if (type.isInstance(obj)) {
                return true;
            }
        }
        return false;
    }
}
