package compiler.visitors.eval.values.memory;

import compiler.parser.Modifier;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

import java.util.Set;

public class Variable {
    private AbstractValue value;
    private final boolean isPublic;
    private final boolean isStatic;
    private final boolean isFinal;

    public Variable(AbstractValue value, Set<Modifier> modifiers) {
        this.value = value;
        this.isPublic = modifiers.contains(Modifier.PUBLIC) || !modifiers.contains(Modifier.STATIC);
        this.isStatic = modifiers.contains(Modifier.STATIC);
        this.isFinal = modifiers.contains(Modifier.FINAL);
    }

    public Variable(AbstractValue value) {
        this.value = value;
        this.isPublic = true;
        this.isStatic = false;
        this.isFinal = false;
    }

    // Bequeme Helper-Methoden für die Logik in den Closures
    public boolean isFinal() {
        return isFinal;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPrivate() {
        return !isPublic;
    }

    public AbstractValue getValue() {
        return value;
    }

    public void setValue(AbstractValue newValue) {
        if (isFinal()) {
            throw new EvalException("Cannot reassign final variable.");
        }
        this.value = newValue;
    }
}