package compiler.visitors.eval.values.memory;

import compiler.visitors.eval.values.AbstractValue;

public class ClassClosure extends Closure {

    public ClassClosure(Closure parent) {
        super(parent);
    }

    /**
     * Sucht ein statisches Feld in der Klassenhierarchie.
     */
    public AbstractValue getValueClass(String name) {
        if (existsHere(name)) return values.get(name).getValue();
        if (parent instanceof ClassClosure) {
            return ((ClassClosure) parent).getValueClass(name);
        }
        return null;
    }
}