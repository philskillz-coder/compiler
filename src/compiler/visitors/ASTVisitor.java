package compiler.visitors;

import compiler.ast.*;

public interface ASTVisitor<R> {
    R visitProgramNode(ProgramNode node);
    R visitVariableDecl(VariableDeclNode node);
    R visitVariableDef(VariableDefNode node);
    R visitVariableAssn(VariableAssnNode node);

    R visitIntLiteral(LiteralIntNode node);
    R visitFloatLiteral(LiteralFloatNode node);
    R visitStringLiteral(LiteralStringNode node);

    R visitBinaryOp(BinaryOpNode node);
    R visitUnaryOp(UnaryOpNode node);
    R visitIfStmt(IfStmtNode node);
    R visitWhileStmt(WhileStmtNode node);

    R visitBlockExpr(BlockExpr node);
    R visitBlockStmt(BlockStmt node);
    R visitFunctionDecl(FunctionDeclNode node);
    R visitFunctionCall(FunctionCallNode node);
    R visitReturn(ReturnStmtNode node);
    R visitResult(ResultStmtNode node);
    R visitExprStmt(ExprStmtNode node);
    R visitIdentifierNode(IdentifierNode node);
}
