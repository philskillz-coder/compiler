package compiler.visitors.eval.values.complex;

import compiler.visitors.eval.values.ComplexValue;
import compiler.parser.ast.FunctionDecl;
import compiler.visitors.eval.values.memory.Closure;

public class FunctionValue extends ComplexValue {
    private final FunctionDecl decl;
    private final Closure closure;

    public FunctionValue(FunctionDecl decl, Closure closure) {
        this.decl = decl;
        this.closure = closure;
    }

    public FunctionDecl getDecl() {
        return decl;
    }

    public Closure getClosure() {
        return closure;
    }

    @Override
    public Object getNativeAbstractValue() { return this; }

    @Override
    public String toString() { return "<Function " + decl.name + ">"; }
}
