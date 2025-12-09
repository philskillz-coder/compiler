package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableAssnNode extends Expr {
    public final IdentifierNameNode name;
    public final Expr newValue;

    public VariableAssnNode(IdentifierNameNode name, Expr newValue) {
        this.name = name;
        this.newValue = newValue;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableAssn(this);
    }
}
