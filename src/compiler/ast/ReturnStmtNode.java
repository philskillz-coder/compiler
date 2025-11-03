package compiler.ast;

import compiler.visitors.ASTVisitor;

public class ReturnStmtNode extends Stmt {
    public final Expr returnValue;

    public ReturnStmtNode(Expr returnValue) {
        this.returnValue = returnValue;
    }

    public ReturnStmtNode() {
        this.returnValue = null;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}
