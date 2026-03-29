package compiler.parser.ast;

import compiler.parser.Modifier;
import compiler.visitors.ASTVisitor;

import java.util.Set;

public class VariableDecl extends Stmt {
    public final Type type;
    public final String name;
    public final Set<Modifier> modifiers;

    public VariableDecl(Type type, String name, Set<Modifier> modifiers) {
        this.type = type;
        this.name = name;
        this.modifiers = modifiers;
    }

    public VariableDecl(Type type, String name) {
        this(type, name, Set.of());
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVariableDecl(this);
    }
}