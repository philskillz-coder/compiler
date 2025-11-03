package compiler.ast;

import compiler.visitors.ASTVisitor;

public class BinaryOpNode extends Expr {
    public final BinaryOperator op;
    public final Expr lhs;
    public final Expr rhs;

    public BinaryOpNode(BinaryOperator op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBinaryOp(this);
    }
}
