package compiler.parser.ast;


import compiler.visitors.ASTVisitor;

public class IfStmtNode extends Stmt {
    public final Expr condition;
    public final Stmt thenStatement; // Der Code, wenn die Bedingung wahr ist (kann BlockStmt oder Einzel-Stmt sein)
    public final Stmt elseBranch;    // Der optionale 'else'-Teil (kann null, ein BlockStmt oder ein weiterer IfStmtNode sein)

    public IfStmtNode(Expr condition, Stmt thenStatement, Stmt elseBranch) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        // Da es nur noch eine Klasse gibt, brauchen Sie nur eine Visit-Methode
        return visitor.visitIfStmt(this);
    }
}