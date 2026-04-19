package compiler.visitors.eval.values.complex;

import compiler.parser.ast.BinaryOperator;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.values.ComplexValue;
import compiler.visitors.eval.values.literal.BoolValue;
import compiler.visitors.eval.values.literal.NullValue;
import compiler.visitors.eval.values.memory.ObjectClosure;

public class ObjectValue extends ComplexValue {
    // Nicht final, damit wir das Henne-Ei-Problem beim Instanziieren lösen können
    private ObjectClosure closure;

    public ObjectValue() {
        // Leerer Konstruktor für die erste Phase der Erstellung
    }

    public ObjectValue(ObjectClosure closure) {
        this.closure = closure;
    }

    public void setClosure(ObjectClosure closure) {
        this.closure = closure;
    }

    public ObjectClosure getClosure() {
        return closure;
    }

    @Override
    public Object getNativeAbstractValue() { return this; }

    @Override
    public String toString() {
        return "Object@" + Integer.toHexString(hashCode());
    }
}