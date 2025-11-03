package compiler.ast;

import compiler.visitors.ASTVisitor;

import java.util.List;

public class FunctionDeclNode extends Stmt {
    public final IdentifierNode name;
    public final List<VariableDeclNode> parameters;
    public final Stmt body;
    public final IdentifierNode returnType;
    public final boolean isBuiltin;

    public FunctionDeclNode(IdentifierNode name, List<VariableDeclNode> parameters, Stmt body, IdentifierNode returnType) {
        this(name, parameters, body, returnType, false);
    }

    public FunctionDeclNode(IdentifierNode name, List<VariableDeclNode> parameters, Stmt body, IdentifierNode returnType, boolean isBuiltin) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
        this.isBuiltin = isBuiltin; // <--- Initialisiert
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunctionDecl(this);
    }
}
