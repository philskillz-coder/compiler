package compiler.visitors.eval.values.complex;

import compiler.parser.ast.ClassDecl;
import compiler.visitors.eval.values.ComplexValue;
import compiler.visitors.eval.values.memory.ClassClosure;
import compiler.visitors.eval.values.memory.Closure;

public class ClassValue extends ComplexValue {
    private final ClassDecl classDecl;
    private final ClassClosure closure;

    public ClassValue(ClassDecl decl, ClassClosure closure) {
        this.classDecl = decl;
        this.closure = closure;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this;
    }

    public ClassDecl getDecl() {
        return classDecl;
    }

    public ClassClosure getClosure() {
        return closure;
    }

    @Override
    public String toString() {
        return "<Class " + classDecl.toString() + ">";
    }
}