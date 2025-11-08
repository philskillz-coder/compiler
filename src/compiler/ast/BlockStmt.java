package compiler.ast;

import compiler.visitors.ASTVisitor;

import java.util.Collections;
import java.util.List;

public class BlockStmt extends Stmt {
    public final List<Stmt> statements;
//    public Environment environment;

    public BlockStmt() {
        this.statements = Collections.emptyList();
    }

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBlockStmt(this);
    }
}
