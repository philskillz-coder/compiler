package compiler.ast;

import compiler.visitors.ASTVisitor;

public class ExprStmtNode extends Stmt {
    public final Expr expr;

    public ExprStmtNode(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitExprStmt(this);
    }
}
