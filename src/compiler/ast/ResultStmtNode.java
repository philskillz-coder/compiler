package compiler.ast;

import compiler.visitors.ASTVisitor;

public class ResultStmtNode extends Stmt {
    public final Expr resultValue;

    public ResultStmtNode(Expr resultValue) {
        this.resultValue = resultValue;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitResult(this);
    }
}
