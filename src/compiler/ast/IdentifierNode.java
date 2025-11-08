package compiler.ast;

import compiler.visitors.ASTVisitor;

public class IdentifierNode extends Expr {
    public String identifier;

    public IdentifierNode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIdentifier(this);
    }
}
