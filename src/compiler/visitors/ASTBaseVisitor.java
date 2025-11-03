package compiler.visitors;

import compiler.ast.*;

public abstract class ASTBaseVisitor<R> implements ASTVisitor<R> {
    @Override public R visitProgramNode(ProgramNode node) {return null;}
    @Override public R visitVariableDef(VariableDefNode node) { return null; }

    @Override
    public R visitVariableDecl(VariableDeclNode node) {
        return null;
    }

    @Override
    public R visitVariableAssn(VariableAssnNode node) {
        return null;
    }

    @Override public R visitBinaryOp(BinaryOpNode node) { return null; }
    @Override public R visitIfStmt(IfStmtNode node) { return null; }
    @Override public R visitWhileStmt(WhileStmtNode node) { return null; }

    @Override
    public R visitFunctionDecl(FunctionDeclNode node) { return null; }

    @Override
    public R visitBlockExpr(BlockExpr node) {
        return null;
    }

    @Override
    public R visitBlockStmt(BlockStmt node) {
        return null;
    }

    @Override
    public R visitResult(ResultStmtNode node) {
        return null;
    }

    @Override
    public R visitFunctionCall(FunctionCallNode node) {
        return null;
    }

    @Override
    public R visitReturn(ReturnStmtNode node) {
        return null;
    }

    @Override
    public R visitExprStmt(ExprStmtNode node) {
        return null;
    }

    @Override
    public R visitUnaryOp(UnaryOpNode node) { return null; }

    @Override
    public R visitIdentifierNode(IdentifierNode node) {
        return null;
    }

    @Override
    public R visitIntLiteral(LiteralIntNode node) {
        return null;
    }

    @Override
    public R visitFloatLiteral(LiteralFloatNode node) {
        return null;
    }

    @Override
    public R visitStringLiteral(LiteralStringNode node) {
        return null;
    }
}
