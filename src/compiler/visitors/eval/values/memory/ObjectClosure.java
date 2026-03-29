package compiler.visitors.eval.values.memory;

import compiler.parser.Modifier;
import compiler.visitors.eval.values.AbstractValue;

import java.util.Set;

public class ObjectClosure extends Closure {

    public ObjectClosure(ClassClosure classParent, AbstractValue selfAsObjectValue) {
        super(classParent);
        // 'this' ist eine finale Variable, die auf das Objekt selbst zeigt
        defineHere("this", new Variable(selfAsObjectValue, Set.of(Modifier.FINAL)));
    }

    /**
     * Die Haupt-Suchlogik für den Punkt-Operator (obj.field)
     */
    public AbstractValue getValueObject(String name) {
        // 1. Instanz-Attribut?
        if (existsHere(name)) {
            return getValue(name);
        }
        // 2. Statisches Attribut in der Klasse/Superklasse?
        if (parent instanceof ClassClosure) {
            return ((ClassClosure) parent).getValueClass(name);
        }
        return null;
    }

    /**
     * Die Logik für Zuweisungen (obj.field = value)
     */
    public void setValueObject(String name, AbstractValue newValue) {
        // 1. Wenn es ein lokales Instanz-Feld ist: überschreiben
        if (existsHere(name)) {
            values.get(name).setValue(newValue);
            return;
        }

        // 2. Wenn es ein statisches Feld der Klasse ist: dort überschreiben
        if (parent instanceof ClassClosure) {
            parent.reassignHere(name, newValue);
        }

        // 3. Fallback: Neues Instanz-Feld anlegen (Default-Verhalten)
        defineHere(name, new Variable(newValue)); // todo: keine ahnung
    }
}