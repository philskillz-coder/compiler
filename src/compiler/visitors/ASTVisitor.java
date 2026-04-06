package compiler.visitors;

import compiler.parser.ast.*;

public interface ASTVisitor<R> {
    // Programm
    R visitProgram(Program node);

    // Variablen
    R visitVariableDecl(VariableDecl node);
    R visitVariableDef(VariableDef node);

    // Literale
    R visitLiteralInt(LiteralInt node);
    R visitLiteralFloat(LiteralFloat node);
    R visitLiteralString(LiteralString node);
    R visitLiteralBool(LiteralBool node);

    // Operatoren
    R visitBinaryOp(BinaryOp node);
    R visitUnaryOp(UnaryOp node);
    R visitAssign(AssignExpr node);

    // Statements
    R visitIf(IfStmt node);
    R visitWhile(WhileStmt node);
    R visitReturn(ReturnStmt node);
    R visitYield(YieldStmt node);
    R visitBreak(BreakStmt node);
    R visitContinue(ContinueStmt node);
    R visitExpr(ExprStmt node);
    R visitBlock(Block node);
    R visitYieldBlock(YieldBlock node);

    // Funktionen & Klassen
    R visitFunctionDecl(FunctionDecl node);
    R visitFunctionCall(FunctionCall node);
    R visitClassDecl(ClassDecl node);

    // Variable references
    R visitVariableExpr(VariableExpr node);
    R visitFieldAccessExpr(FieldAccessExpr node);
}
