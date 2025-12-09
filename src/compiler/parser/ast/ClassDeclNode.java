package compiler.parser.ast;


import compiler.visitors.ASTVisitor;

public class ClassDeclNode extends Stmt {
    public final IdentifierAccessNode accessModifier;
    public final IdentifierBoundsNode boundsModifier;
    public final IdentifierNameNode name;
    public final BlockStmt body;

    public ClassDeclNode(IdentifierAccessNode accessModifier, IdentifierBoundsNode boundsModifier, IdentifierNameNode name, BlockStmt body) {
        this.accessModifier = accessModifier;
        this.boundsModifier = boundsModifier;
        this.name = name;
        this.body = body;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitClassDecl(this);
    }
}
