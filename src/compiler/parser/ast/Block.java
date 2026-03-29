package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.Collections;
import java.util.List;

public class Block extends Stmt {
    public final List<Stmt> statements;

    public Block() {
        this.statements = Collections.emptyList();
    }

    public Block(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBlock(this);
    }
}