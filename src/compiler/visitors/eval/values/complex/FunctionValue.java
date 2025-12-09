package compiler.visitors.eval.values.complex;

import compiler.parser.ast.FunctionDeclNode;
import compiler.visitors.eval.values.ComplexValue;

public class FunctionValue extends ComplexValue {
    private final FunctionDeclNode functionNode;

    public FunctionValue(FunctionDeclNode functionNode) {
        this.functionNode = functionNode;
    }

    @Override
    public Object getNativeAbstractValue() {
        return this.functionNode;
    }
}
