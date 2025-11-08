package compiler.ast;

import compiler.visitors.ASTVisitor;

public class LiteralIntNode extends LiteralNode {
    public final int value;

    public LiteralIntNode(int value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralInt(this);
    }
}
