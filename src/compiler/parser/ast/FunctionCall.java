package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class FunctionCall extends Expr {
    public final Expr callee;
    public final List<Expr> arguments;

    public FunctionCall(Expr callee, List<Expr> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionCall(this);
    }
}
