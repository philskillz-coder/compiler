package compiler.parser.ast;


import compiler.visitors.ASTVisitor;

import java.util.List;

public class ClassDecl extends Stmt {
    public final String name;
    public final List<VariableDecl> fields;
    public final List<FunctionDecl> methods;
    public final List<ClassDecl> innerClasses;

    public ClassDecl(String name, List<VariableDecl> fields, List<FunctionDecl> methods, List<ClassDecl> innerClasses) {
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