package compiler.visitors.eval.values;

public class FunctionValue extends ComplexValue {
    private final String functionName;

    public FunctionValue(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.functionName;
    }
}
