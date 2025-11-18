package compiler.visitors.eval;

import compiler.ast.BlockStmt;

public abstract class VirtualBlockExpr extends BlockStmt {
    public abstract EvalResult execute(Environment env);
}
