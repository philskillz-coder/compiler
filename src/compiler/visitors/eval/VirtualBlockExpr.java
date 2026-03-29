package compiler.visitors.eval;

import compiler.parser.ast.Block;
import compiler.visitors.eval.values.memory.Closure;

public abstract class VirtualBlockExpr extends Block {
    public abstract EvalResult execute(Closure closure);
}
