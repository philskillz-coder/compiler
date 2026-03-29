package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralFloat extends Literal {
    public final float value;

    public LiteralFloat(float value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralFloat(this);
    }
}
