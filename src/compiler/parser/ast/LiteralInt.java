package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralInt extends Literal {
    public final int value;

    public LiteralInt(int value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralInt(this);
    }
}
