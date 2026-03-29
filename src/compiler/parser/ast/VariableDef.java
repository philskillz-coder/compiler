package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableDef extends VariableDecl {
    public final Expr initialValue;

    public VariableDef(Type type, String name, Expr initialValue, boolean isFinal) {
        super(type, name, isFinal);
        this.initialValue = initialValue;
    }

    public VariableDef(Type type, String name, Expr initialValue) {
        this(type, name, initialValue, false);
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDef(this);
    }
}