package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableDecl extends Stmt {
    public final Type type;
    public final String name;
    public final boolean isFinal;

    public VariableDecl(Type type, String name, boolean isFinal) {
        this.type = type;
        this.name = name;
        this.isFinal = isFinal;
    }

    public VariableDecl(Type type, String name) {
        this(type, name, false);
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}