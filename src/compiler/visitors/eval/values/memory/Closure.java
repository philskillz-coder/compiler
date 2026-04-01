package compiler.visitors.eval.values.memory;

import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.AbstractValue;

import java.util.HashMap;
import java.util.Map;

public class Closure {
    protected final Closure parent;
    protected final Map<String, Variable> values = new HashMap<>();

    public Closure(Closure parent) {
        this.parent = parent;
    }

    public Closure() {
        this.parent = null;
    }


    public AbstractValue getValue(String name) {
        return values.get(name).getValue();
    }

    public AbstractValue getValueLookup(String name) {
        if (existsHere(name)) return getValue(name);
        else if (parent != null) return parent.getValueLookup(name);
        return null;
    }

    public void defineHere(String name, Variable value) {
        if (existsHere(name)) {
            throw new EvalException("Duplicate variable name (define): " + name);
        }
        values.put(name, value);
    }

    public void reassignHere(String name, AbstractValue value) {
        if (!existsHere(name)) {
            throw new EvalException("Variable name not found (reassign): " + name);
        }
        Variable v = values.get(name);
        if (v != null) v.setValue(value);
    }

    public void reassign(String name, AbstractValue value) {
        // 1. Schauen, ob die Variable direkt hier im aktuellen Scope ist
        if (existsHere(name)) {
            values.get(name).setValue(value); // Nutzt Variable.setValue (inkl. Final-Check)
            return;
        }

        // 2. Wenn nicht hier, frage den Parent (z.B. das Objekt oder die Klasse)
        if (parent != null) {
            parent.reassign(name, value);
        } else {
            // 3. Wenn wir ganz oben (Global) sind und nichts gefunden haben: Fehler
            throw new EvalException("Variable '" + name + "' is not defined and cannot be reassigned.");
        }
    }

    public boolean existsHere(String name) {
        return values.containsKey(name);
    }

}