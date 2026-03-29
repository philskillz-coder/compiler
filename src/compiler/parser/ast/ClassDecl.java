package compiler.parser.ast;


import compiler.lexer.TokenType;
import compiler.parser.Modifier;
import compiler.visitors.ASTVisitor;

import java.util.List;
import java.util.Set;

public class ClassDecl extends Stmt {
    public final String name;
    public final List<VariableDecl> fields;
    public final List<FunctionDecl> methods;
    public final List<ClassDecl> innerClasses;
    public final Set<Modifier> modifiers;

    public ClassDecl(Set<Modifier> modifiers, String name, List<VariableDecl> fields, List<FunctionDecl> methods, List<ClassDecl> innerClasses) {
        this.modifiers = modifiers;
        this.name = name;
        this.fields = fields;
        this.methods = methods;
        this.innerClasses = innerClasses;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitClassDecl(this);
    }
}