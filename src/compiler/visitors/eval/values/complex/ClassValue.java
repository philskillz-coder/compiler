package compiler.visitors.eval.values.complex;

import compiler.parser.ast.ClassDecl;
import compiler.parser.ast.FunctionDecl;
import compiler.parser.ast.VariableDecl;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.ComplexValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassValue extends ComplexValue {
    private final String name;

    public ClassValue(ClassDecl decl) {
        this.name = decl.name;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }
}