package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class WhileStmt extends Stmt {
    public final Expr condition;
    public final Stmt body;

    public WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitWhile(this);
    }
}
