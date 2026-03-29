package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class FunctionDecl extends Stmt {
    public final Type returnType;
    public final String name;
    public final List<VariableDecl> parameters;
    public final Block body;

    public FunctionDecl(Type returnType, String name, List<VariableDecl> parameters, Block body) {
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