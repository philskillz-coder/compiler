package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableDeclNode extends Stmt {
    public final IdentifierNameNode storageModifier; // static
    public final IdentifierNameNode typeIdentifier;
    public final IdentifierNameNode name;

    public VariableDeclNode(IdentifierNameNode storageModifier, IdentifierNameNode typeIdentifier, IdentifierNameNode name) {
        this.storageModifier = storageModifier;
        this.typeIdentifier = typeIdentifier;
        this.name = name;
    }

    public VariableDeclNode(IdentifierNameNode typeIdentifier, IdentifierNameNode name) {
        this(null, typeIdentifier, name);
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
