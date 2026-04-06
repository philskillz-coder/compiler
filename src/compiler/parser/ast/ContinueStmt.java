package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class ContinueStmt extends Stmt {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitContinue(this);
    }
}
