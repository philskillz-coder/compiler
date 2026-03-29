package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class ExprStmt extends Stmt {
    public final Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitExpr(this);
    }
}