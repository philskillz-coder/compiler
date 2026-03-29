package compiler.visitors.eval;

import compiler.parser.ast.*;
import compiler.visitors.ASTVisitor;
import compiler.visitors.eval.exceptions.NotImplementedException;
import compiler.visitors.eval.values.*;
import compiler.visitors.eval.values.complex.FunctionValue;
import compiler.visitors.eval.values.literal.*;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.memory.Closure;

import java.util.*;

public class ASTEvalVisitor implements ASTVisitor<EvalResult> {

    private final Deque<Closure> closures = new ArrayDeque<>();

    public ASTEvalVisitor() {
        Closure root = new Closure();
        root.setValue("print", new FunctionValue(new Print(), root));

        closures.push(root);
    }

    private void enterClosure() { closures.push(new Closure(closures.peek())); }
    private void enterClosure(Closure closure) { closures.push(closure); }
    private void exitClosure() { closures.pop(); }

    private void assign(String name, AbstractValue value) {
        assert closures.peek() != null;
        closures.peek().setValue(name, value);
    }

    private AbstractValue resolve(String name) {
        assert closures.peek() != null;
        AbstractValue value = closures.peek().getValueParent(name);
        if (value != null) return value;
        throw new EvalException("Variable not defined: " + name);
    }

    @Override
    public EvalResult visitProgram(Program node) {
        EvalResult last = EvalResult.nullValue();
        for (Stmt stmt : node.statements) {
            last = stmt.accept(this);
            if (last.isBreaking()) return last;
        }
        return last;
    }

    @Override
    public EvalResult visitBlock(Block node) {
        if (node instanceof VirtualBlockExpr) {
            return ((VirtualBlockExpr) node).execute(closures.peek());
        }

        enterClosure();
        try {
            EvalResult last = EvalResult.nullValue();
            for (Stmt stmt : node.statements) {
                last = stmt.accept(this);
                if (last.isBreaking()) break;
            }
            return last;
        } finally {
            exitClosure();
        }
    }

    @Override
    public EvalResult visitVariableDecl(VariableDecl node) {
        assign(node.name, NullValue.getInstance());
        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitVariableDef(VariableDef node) {
        EvalResult value = node.initialValue.accept(this);
        assign(node.name, value.unwrapValue());
        return value;
    }

    @Override
    public EvalResult visitAssign(AssignExpr node) {
        EvalResult value = node.value.accept(this);

        if (node.target instanceof VariableExpr) { // simple variable assignment
            String name = ((VariableExpr) node.target).name;
            if (!closures.peek().setValueParent(name, value.unwrapValue())) {
                throw new EvalException("Variable not defined: " + name);
            }
        } else if (node.target instanceof FieldAccessExpr) { // field assignment
            throw new NotImplementedException("Field assignment not implemented yet");
            /*EvalResult object = ((FieldAccessExpr) node.target).object.accept(this);
            if (!object.isObject()) throw new EvalException("Cannot assign to non-object field");
            object.asObject().setField(((FieldAccessExpr) node.target).field.identifier, value); */
        } else {
            throw new EvalException("Invalid assignment target");
        }

        return value;
    }

    @Override
    public EvalResult visitVariableExpr(VariableExpr node) {
        return EvalResult.value(resolve(node.name));
    }

    @Override
    public EvalResult visitFieldAccessExpr(FieldAccessExpr node) {
        throw new NotImplementedException("Field access expression not implemented yet");
        /* EvalResult object = node.object.accept(this);
        if (!object.isObject()) throw new EvalException("Field access on non-object");
        return object.asObject().get(node.field.identifier); */
    }

    @Override
    public EvalResult visitFunctionDecl(FunctionDecl node) {
        assign(node.name, new FunctionValue(node, closures.peek()));

        return EvalResult.nullValue();
    }

    /*@Override
    public EvalResult visitFunctionCall(FunctionCall node) {
        EvalResult callee = node.callee.accept(this);
        if (!callee.isFunction()) throw new EvalException("Not a function");

        FunctionDecl func = callee.asFunction().getDecl();
        Closure definitionClosure = callee.asFunction().getClosure();

        if (func.parameters.size() != node.arguments.size()) { // todo: make this better, positional arguments?
            throw new EvalException("Argument count mismatch for function " + func.name);
        }

        Closure callClosure = new Closure(definitionClosure);
        enterClosure(callClosure);

        for (int i = 0; i < func.parameters.size(); i++) {
            VariableDecl param = func.parameters.get(i);
            EvalResult argVal = node.arguments.get(i).accept(this);
            assign(param.name, argVal.unwrapValue());
        }

        EvalResult result = func.body.accept(this);
        exitClosure();

        if (result.is(EvalResult.ResultType.RETURN)) return EvalResult.value(result.unwrapReturnValue());
        return result;
    } */
    @Override
    public EvalResult visitFunctionCall(FunctionCall node) {
        // 1. Zuerst die Funktion suchen, SOLANGE wir noch im alten Scope sind!
        EvalResult callee = node.callee.accept(this);
        if (!callee.isFunction()) throw new EvalException("Not a function");

        FunctionDecl func = callee.asFunction().getDecl();
        Closure definitionClosure = callee.asFunction().getClosure();

        if (func.parameters.size() != node.arguments.size()) {
            throw new EvalException("Argument count mismatch for function " + func.name);
        }

        // 2. Argumente auswerten, SOLANGE wir noch im alten Scope sind!
        // (Sonst könnten Argumente keine Variablen von "außen" lesen)
        List<AbstractValue> evaluatedArgs = new ArrayList<>();
        for (Expr arg : node.arguments) {
            evaluatedArgs.add(arg.accept(this).unwrapValue());
        }

        // 3. Jetzt erst in den neuen Scope wechseln
        Closure callClosure = new Closure(definitionClosure);
        enterClosure(callClosure);

        // 4. Parameter in der neuen Closure zuweisen
        for (int i = 0; i < func.parameters.size(); i++) {
            assign(func.parameters.get(i).name, evaluatedArgs.get(i));
        }

        // 5. Body ausführen
        EvalResult result = func.body.accept(this);
        exitClosure();

        if (result.is(EvalResult.ResultType.RETURN)) {
            return EvalResult.value(result.unwrapReturnValue());
        }
        return result;
    }

    @Override
    public EvalResult visitClassDecl(ClassDecl node) {
        throw new NotImplementedException("Class declaration not implemented yet");

        /* Map<String, EvalResult> fields = new HashMap<>();
        Map<String, EvalResult> methods = new HashMap<>();

        for (VariableDecl field : node.fields) {
            fields.put(field.name, EvalResult.defaultValue(field.type.name));
        }
        for (FunctionDecl method : node.methods) {
            methods.put(method.name, EvalResult.value(new FunctionValue(method, new HashMap<>(scopes.peek()))));
        }

        ObjectValue obj = new ObjectValue();
        obj.putAll(fields);
        obj.putAll(methods);

        define(node.name, EvalResult.value(obj));
        return EvalResult.nullValue(); */
    }

    @Override
    public EvalResult visitLiteralInt(LiteralInt node) { return EvalResult.fromInt(node.value); }
    @Override
    public EvalResult visitLiteralFloat(LiteralFloat node) { return EvalResult.fromFloat(node.value); }
    @Override
    public EvalResult visitLiteralBool(LiteralBool node) { return EvalResult.fromBool(node.value); }
    @Override
    public EvalResult visitLiteralString(LiteralString node) { return EvalResult.fromString(node.value); }
    @Override
    public EvalResult visitLiteralNull(LiteralNull node) { return EvalResult.value(NullValue.getInstance()); }

    @Override
    public EvalResult visitBinaryOp(BinaryOp node) {
        EvalResult left = node.lhs.accept(this);
        EvalResult right = node.rhs.accept(this);
        return left.applyBinary(node.op, right);
    }

    @Override
    public EvalResult visitUnaryOp(UnaryOp node) {
        EvalResult operand = node.value.accept(this);
        return operand.applyUnary(node.op);
    }

    @Override
    public EvalResult visitIf(IfStmt node) {
        EvalResult cond = node.condition.accept(this);
        if ((boolean) cond.asBoolean().getNativeAbstractValue()) return node.thenBranch.accept(this);
        if (node.elseBranch != null) return node.elseBranch.accept(this);
        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitWhile(WhileStmt node) {
        EvalResult last = EvalResult.nullValue();
        while ((boolean) node.condition.accept(this).asBoolean().getNativeAbstractValue()) {
            last = node.body.accept(this);
            if (last.isBreaking()) break;
        }
        return last;
    }

    @Override
    public EvalResult visitReturn(ReturnStmt node) {
        if (node.returnValue != null) return EvalResult.returnValue(node.returnValue.accept(this).unwrapValue());
        return EvalResult.returnValue(null);
    }

    @Override
    public EvalResult visitExpr(ExprStmt node) {
        return node.expr.accept(this);
    }
}
