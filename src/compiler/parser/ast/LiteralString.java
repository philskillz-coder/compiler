package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralString extends Literal {
    public final String value;

    public LiteralString(String value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralString(this);
    }
}
