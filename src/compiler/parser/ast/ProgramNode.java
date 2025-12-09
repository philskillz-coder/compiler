package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class ProgramNode extends ASTNode {
    public List<Stmt> nodes;

    public ProgramNode(List<Stmt> nodes) {
        this.nodes = nodes;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgramNode(this);
    }
}
