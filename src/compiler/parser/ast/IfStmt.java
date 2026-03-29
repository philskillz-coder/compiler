package compiler.parser.ast;


import compiler.visitors.ASTVisitor;

public class IfStmt extends Stmt {
    public final Expr condition;
    public final Stmt thenBranch;
    public final Stmt elseBranch; // optional, null if absent

    public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIf(this);
    }
}