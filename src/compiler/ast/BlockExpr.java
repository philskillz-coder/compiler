package compiler.ast;

import compiler.visitors.ASTVisitor;

import java.util.Collections;
import java.util.List;

public class BlockExpr extends Expr {
    public final List<Stmt> statements;

    public BlockExpr() {
        this.statements = Collections.emptyList();
    }

    public BlockExpr(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBlockExpr(this);
    }
}
