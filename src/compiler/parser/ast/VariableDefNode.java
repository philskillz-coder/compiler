package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableDefNode extends VariableDeclNode {
    public final Expr initialValue;

    public VariableDefNode(IdentifierNameNode storageModifier, IdentifierNameNode type, IdentifierNameNode name, Expr initialValue) {
        super(storageModifier, type, name);
        this.initialValue = initialValue;
    }

    public VariableDefNode(IdentifierNameNode type, IdentifierNameNode name, Expr initialValue) {
        this(null, type, name, initialValue);
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDef(this);
    }
}
