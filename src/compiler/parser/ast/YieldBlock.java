package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.Collections;
import java.util.List;

public class YieldBlock extends Expr {
    public final List<Stmt> statements;

    public YieldBlock() {
        this.statements = Collections.emptyList();
    }

    public YieldBlock(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitYieldBlock(this);
    }
}
