package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class YieldStmt extends Stmt {
    public final Expr yieldValue; // null if void

    public YieldStmt(Expr yieldValue) {
        this.yieldValue = yieldValue;
    }

    public YieldStmt() {
        this.yieldValue = null;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitYield(this);
    }
}
