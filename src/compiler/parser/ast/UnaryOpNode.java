package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class UnaryOpNode extends Expr {
    public final UnaryOperator op;
    public final Expr value;

    public UnaryOpNode(UnaryOperator op, Expr value) {
        this.op = op;
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitUnaryOp(this);
    }
}
