package compiler.ast.builtins;

import compiler.ast.BlockStmt;
import compiler.ast.Environment;

public abstract class VirtualBlock extends BlockStmt {
    public abstract Object execute(Environment env);
}
