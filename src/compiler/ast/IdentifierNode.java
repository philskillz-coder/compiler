package compiler.ast;

import compiler.visitors.ASTVisitor;

public class IdentifierNode extends Expr {
    public final String identifier;

    public IdentifierNode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIdentifierNode(this);
    }
}
