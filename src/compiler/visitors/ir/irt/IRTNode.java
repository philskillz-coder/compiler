package compiler.visitors.ir.irt;

import compiler.parser.ast.ASTNode;

public class IRTNode {
    public final ASTNode astReference;

    public IRTNode(ASTNode astReference) {
        this.astReference = astReference;
    }
}
