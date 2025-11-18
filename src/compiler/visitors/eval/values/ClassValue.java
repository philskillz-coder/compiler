package compiler.visitors.eval.values;

public class ClassValue extends ComplexValue {

    public ClassValue(String className) {
        this.className = className;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.className;
    }
}
