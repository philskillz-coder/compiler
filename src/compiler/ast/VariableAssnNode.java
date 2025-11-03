package compiler.ast;

import compiler.visitors.ASTVisitor;

public class VariableAssnNode extends Expr {
    public final IdentifierNode name;
    public final Expr newValue;

    public VariableAssnNode(IdentifierNode name, Expr newValue) {
        this.name = name;
        this.newValue = newValue;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableAssn(this);
    }
}
