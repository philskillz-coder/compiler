package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class CompoundAssignExpr extends Expr {
    public final Expr target; // must be lvalue (VariableExpr, FieldAccessExpr)
    public final BinaryOperator op;
    public final Expr value;

    public CompoundAssignExpr(BinaryOperator op, Expr target, Expr value) {
        this.target = target;
        this.op = op;
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitCompoundAssign(this);
    }
}