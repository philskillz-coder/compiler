package compiler.ast;

import compiler.visitors.ASTVisitor;

public abstract class ASTNode {
    public abstract <R> R accept(ASTVisitor<R> visitor);
}
