package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class WhileStmtNode extends Stmt {
    public final Expr condition;
    public final Stmt body;

    public WhileStmtNode(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitWhileStmt(this);
    }
}
