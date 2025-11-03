package compiler.ast;

import compiler.visitors.ASTVisitor;

public class VariableDefNode extends VariableDeclNode {
    public final Expr initialValue;

    public VariableDefNode(IdentifierNode type, IdentifierNode name, Expr initialValue) {
        super(type, name);
        this.initialValue = initialValue;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDef(this);
    }
}
