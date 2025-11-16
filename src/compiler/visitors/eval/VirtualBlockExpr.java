package compiler.visitors.eval;

import compiler.ast.BlockStmt;
import compiler.visitors.eval.values.AbstractValue;

public abstract class VirtualBlockExpr extends BlockStmt {
    public abstract EvalResult execute(Environment env);
}
