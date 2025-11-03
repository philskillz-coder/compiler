package compiler.ast;

import compiler.visitors.ASTVisitor;

public class VariableDeclNode extends Stmt {
    public final IdentifierNode typeIdentifier;
    public final IdentifierNode name;

    public VariableDeclNode(IdentifierNode typeIdentifier, IdentifierNode name) {
        this.typeIdentifier = typeIdentifier;
        this.name = name;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
