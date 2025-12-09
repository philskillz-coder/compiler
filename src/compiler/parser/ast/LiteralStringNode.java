package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralStringNode extends LiteralNode {
    public final String value;

    public LiteralStringNode(String value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralString(this);
    }
}
