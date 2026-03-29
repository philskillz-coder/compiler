package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class LiteralNull extends Literal {

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralNull(this);
    }
}
