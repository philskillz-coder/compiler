package compiler.ast.builtins;

import compiler.ast.BlockStmt;
import compiler.ast.Environment;

public abstract class VirtualBlockExpr extends BlockStmt {
    public abstract Object execute(Environment env);
}
