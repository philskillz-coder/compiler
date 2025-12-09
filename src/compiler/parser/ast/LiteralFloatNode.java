package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralFloatNode extends LiteralNode {
    public final float value;

    public LiteralFloatNode(float value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralFloat(this);
    }
}
