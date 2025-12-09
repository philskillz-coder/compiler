package compiler.visitors.eval;

import compiler.parser.ast.BlockStmt;

public abstract class VirtualBlockExpr extends BlockStmt {
    public abstract EvalResult execute(Environment env);
}
