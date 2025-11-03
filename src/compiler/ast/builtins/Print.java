package compiler.ast.builtins;

import compiler.ast.*;

import java.util.Collections;

public class Print extends FunctionDeclNode {

    public Print() {
        super(
                new IdentifierNode("print"),
                Collections.singletonList(new VariableDeclNode(
                        new IdentifierNode("string"),
                        new IdentifierNode("text")
                )),
                new VirtualBlockExpr() {
                    @Override
                    public Object execute(Environment env) {
                        System.out.println(env.getVar("text"));
                        return null;
                    }
                },
                new IdentifierNode("void"),
                true
        );
    }
}
