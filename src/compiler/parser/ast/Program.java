package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class Program extends ASTNode {
    public final List<Stmt> statements;

    public Program(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgram(this);
    }
}