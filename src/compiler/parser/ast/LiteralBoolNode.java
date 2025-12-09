package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralBoolNode extends LiteralNode {
    public final boolean value;

    public LiteralBoolNode(boolean value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralBool(this);
    }
}
