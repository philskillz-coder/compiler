package compiler.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class FunctionCallNode extends Expr {
    public final Expr callee;
    public final List<Expr> parameters;

    public FunctionCallNode(Expr callee, List<Expr> parameters) {
        this.callee = callee;
        this.parameters = parameters;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionCall(this);
    }
}
