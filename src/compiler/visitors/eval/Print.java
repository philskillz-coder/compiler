package compiler.visitors.eval;

import compiler.ast.*;
import compiler.visitors.eval.values.NullValue;

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
                    public EvalResult execute(Environment env) {
                        System.out.println(env.getVar("text").toString());
                        return EvalResult.returnValue(NullValue.getInstance());
                    }
                },
                new IdentifierNode("void"),
                true
        );
    }
}
