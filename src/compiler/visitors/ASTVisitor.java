package compiler.visitors;

import compiler.parser.ast.*;

public interface ASTVisitor<R> {
    R visitProgramNode(ProgramNode node);
    R visitVariableDecl(VariableDeclNode node);
    R visitVariableDef(VariableDefNode node);
    R visitVariableAssn(VariableAssnNode node); // todo: this is kind of an binary op

    R visitLiteralInt(LiteralIntNode node);
    R visitLiteralFloat(LiteralFloatNode node);
    R visitLiteralString(LiteralStringNode node);
    R visitLiteralBool(LiteralBoolNode node);

    R visitBinaryOp(BinaryOpNode node);
    R visitUnaryOp(UnaryOpNode node);
    R visitIfStmt(IfStmtNode node);
    R visitWhileStmt(WhileStmtNode node);

    R visitBlockExpr(BlockExpr node);
    R visitBlockStmt(BlockStmt node);
    R visitClassDecl(ClassDeclNode node);
    R visitFunctionDecl(FunctionDeclNode node);
    R visitFunctionCall(FunctionCallNode node);
    R visitReturn(ReturnStmtNode node);
    R visitResult(ResultStmtNode node);
    R visitExprStmt(ExprStmtNode node);

    R visitIdentifier(IdentifierNode node);
}
