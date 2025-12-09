package compiler.visitors.eval;

import compiler.parser.ast.FunctionDeclNode;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public enum Visibility {
        NORMAL,
        JUMP
    }

    private final Map<String, AbstractValue> variables = new HashMap<>();
    private final Map<String, FunctionDeclNode> functions = new HashMap<>();

    final Environment parent;
    final Visibility visibility;

    public Environment() {
        this.parent = null;
        this.visibility = Visibility.NORMAL;
    }

    public Environment(Environment parent, Visibility visibility) {
        this.parent = parent;
        this.visibility = visibility;
    }

    public void defineVar(String name, AbstractValue value) {
        variables.put(name, value);
    }

    public AbstractValue getVar(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.getVar(name);
        }
        throw new EvalException("Runtime error: Undefined variable '" + name + "'.");
    }

    public void assignVar(String name, AbstractValue value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null) {
            parent.assignVar(name, value); // todo: assign variable in parent env???
        } else {
            throw new EvalException("Runtime error: Cannot assign to undefined variable '" + name + "'.");
        }
    }

    public boolean varExists(String name, boolean checkParent) {
        if (variables.containsKey(name)) {
            return true;
        }
        if (checkParent && parent != null) {
            return parent.varExists(name, true);
        }
        return false;
    }

    public boolean funcExists(String name, boolean checkParent) {
        if (functions.containsKey(name)) {
            return true;
        }
        if (checkParent && parent != null) {
            return parent.funcExists(name, true);
        }
        return false;
    }

    public boolean assnExists(String name, boolean checkParent) {
        return varExists(name, checkParent) || funcExists(name, checkParent);
    }

    public void defineFunction(String name, FunctionDeclNode fn) {
        functions.put(name, fn);
    }

    public FunctionDeclNode getFunction(String name) {
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        if (parent != null) {
            return parent.getFunction(name);
        }
        throw new EvalException("Runtime error: Undefined function '" + name + "'.");
    }

    public Environment findParentEnvironment() {
        Environment env = this;
        while (env.parent != null && env.visibility == Visibility.JUMP) {
            env = env.parent;
        }
        return env;
    }
}