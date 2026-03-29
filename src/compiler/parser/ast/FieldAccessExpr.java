package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class FieldAccessExpr extends Expr {
    public final Expr target; // e.g., this or another expression
    public final String fieldName;

    public FieldAccessExpr(Expr target, String fieldName) {
        this.target = target;
        this.fieldName = fieldName;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFieldAccessExpr(this);
    }
}