package compiler.parser.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class FunctionDeclNode extends Stmt {
    public final IdentifierNameNode accessModifier;
    public final IdentifierNameNode returnType;
    public final IdentifierNameNode name;
    public final List<VariableDeclNode> parameters;
    public final Stmt body;
    public final boolean isBuiltin;

    public FunctionDeclNode(IdentifierNameNode name, List<VariableDeclNode> parameters, Stmt body, IdentifierNameNode returnType) {
        this(null, name, parameters, body, returnType, false);
    }

    public FunctionDeclNode(IdentifierNameNode name, List<VariableDeclNode> parameters, Stmt body, IdentifierNameNode returnType, boolean isBuiltin) {
        this(null, name, parameters, body, returnType, isBuiltin);
    }

    public FunctionDeclNode(IdentifierNameNode accessModifier, IdentifierNameNode name, List<VariableDeclNode> parameters, Stmt body, IdentifierNameNode returnType, boolean isBuiltin) {
        this.accessModifier = accessModifier;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
        this.isBuiltin = isBuiltin;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDecl(this);
    }
}
