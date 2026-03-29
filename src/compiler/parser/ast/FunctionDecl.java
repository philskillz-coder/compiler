package compiler.parser.ast;

import compiler.lexer.TokenType;
import compiler.parser.Modifier;
import compiler.visitors.ASTVisitor;

import java.util.List;
import java.util.Set;

public class FunctionDecl extends Stmt {
    public final Type returnType;
    public final String name;
    public final List<VariableDecl> parameters;
    public final Block body;
    public final Set<Modifier> modifiers;

    public FunctionDecl(Set<Modifier> modifiers, Type returnType, String name, List<VariableDecl> parameters, Block body) {
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDecl(this);
    }
}