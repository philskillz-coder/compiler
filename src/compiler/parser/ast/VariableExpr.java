package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

public class VariableExpr extends Expr {
    /***
     * Variable expression (lvalue)
     */

    public final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableExpr(this);
    }
}