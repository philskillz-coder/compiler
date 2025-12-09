package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class IdentifierNameNode extends IdentifierNode {
    public IdentifierNameNode(String identifier) {
        super(identifier);
    }
}
