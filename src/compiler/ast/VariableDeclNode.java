package compiler.ast;

import compiler.visitors.ASTVisitor;

public class VariableDeclNode extends Stmt {
    public final IdentifierNode type;
    public final IdentifierNode name;

    public VariableDeclNode(IdentifierNode type, IdentifierNode name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
