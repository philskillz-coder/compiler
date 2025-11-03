package compiler.ast;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, compiler.ast.FunctionDeclNode> functions = new HashMap<>();
    private final Environment parent;

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public boolean varExists(String name) {
        return variables.get(name) != null;
    }

    public void defineVar(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVar(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.getVar(name);
        }
        throw new RuntimeException("Undefined variable: " + name);
    }

    public void assignVar(String name, Object value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null) {
            parent.assignVar(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    public boolean functionExists(String name) {
        return functions.get(name) != null || (parent != null && parent.functionExists(name));
    }

    public void defineFunction(String name, compiler.ast.FunctionDeclNode fn) {
        functions.put(name, fn);
    }

    public compiler.ast.FunctionDeclNode getFunction(String name) {
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        if (parent != null) {
            return parent.getFunction(name);
        }
        throw new RuntimeException("Undefined function: " + name);
    }

    public boolean assnExists(String name) {
        return varExists(name) || functionExists(name);
    }
}
