package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralBool extends Literal {
    public final boolean value;

    public LiteralBool(boolean value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralBool(this);
    }
}
