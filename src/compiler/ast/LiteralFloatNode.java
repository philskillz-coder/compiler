package compiler.ast;

import compiler.visitors.ASTVisitor;

public class LiteralFloatNode extends LiteralNode {
    public final double value;

    public LiteralFloatNode(double value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFloatLiteral(this);
    }
}
