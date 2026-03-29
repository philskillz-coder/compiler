package compiler.visitors.eval.values.memory;

import compiler.visitors.eval.values.AbstractValue;

import java.util.HashMap;
import java.util.Map;

public class Closure {
    private final Closure parent;
    private final Map<String, AbstractValue> values = new HashMap<>();

    public Closure(Closure parent) {
        this.parent = parent;
    }

    public Closure() {
        this.parent = null;
    }


    public AbstractValue getValue(String name) {
        return values.get(name);
    }

    public AbstractValue getValueParent(String name) {
        if (exists(name)) return getValue(name);
        else if (parent != null) return parent.getValueParent(name);
        return null;
    }

    public void setValue(String name, AbstractValue value) {
        values.put(name, value);
    }

    public boolean setValueParent(String name, AbstractValue value) {
        if (exists(name)) {
            values.put(name, value);
            return true;
        } else if (parent != null) {
            return parent.setValueParent(name, value);
        }
        return false;
    }

    public boolean exists(String name) {
        return values.containsKey(name);
    }

    public boolean existsParent(String name) {
        return exists(name) || parent != null && parent.existsParent(name);
    }
}