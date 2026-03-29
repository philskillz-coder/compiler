package compiler.visitors.eval.values.complex;

import compiler.visitors.eval.values.ComplexValue;
import compiler.visitors.eval.values.AbstractValue;
import compiler.visitors.eval.exceptions.EvalException;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends ComplexValue {

    public ObjectValue() {}

    @Override
    public Object getNativeAbstractValue() { return this; }

    @Override
    public String toString() { return "Object"; }
}
