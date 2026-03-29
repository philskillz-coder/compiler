package compiler.parser.ast;

import compiler.lexer.TokenType;
import compiler.parser.Modifier;
import compiler.visitors.ASTVisitor;

import java.util.Set;

public class VariableDef extends VariableDecl {
    public final Expr initialValue;

    public VariableDef(Type type, String name, Expr initialValue, Set<Modifier> modifiers) {
        super(type, name, modifiers);
        this.initialValue = initialValue;
    }

    public VariableDef(Type type, String name, Expr initialValue) {
        this(type, name, initialValue, Set.of());
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDef(this);
    }
}