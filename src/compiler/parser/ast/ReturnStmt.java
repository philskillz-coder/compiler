package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class ReturnStmt extends Stmt {
    public final Expr returnValue; // null if void

    public ReturnStmt(Expr returnValue) {
        this.returnValue = returnValue;
    }

    public ReturnStmt() {
        this.returnValue = null;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}
