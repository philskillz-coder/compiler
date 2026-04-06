package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class BreakStmt extends Stmt {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBreak(this);
    }
}
