package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class AssignExpr extends Expr {
    public final Expr target; // must be lvalue (VariableExpr, FieldAccessExpr)
    public final Expr value;

    public AssignExpr(Expr target, Expr value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitAssign(this);
    }
}