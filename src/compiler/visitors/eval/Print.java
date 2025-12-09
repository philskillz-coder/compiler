package compiler.visitors.eval;

import compiler.parser.ast.*;
import compiler.visitors.eval.values.literal.NullValue;

import java.util.Collections;

public class Print extends FunctionDeclNode {

    public Print() {
        super(
                new IdentifierNameNode("print"),
                Collections.singletonList(new VariableDeclNode(
                        new IdentifierNameNode("string"),
                        new IdentifierNameNode("text")
                )),
                new VirtualBlockExpr() {
                    @Override
                    public EvalResult execute(Environment env) {
                        System.out.println(env.getVar("text").toString());
                        return EvalResult.returnValue(NullValue.getInstance());
                    }
                },
                new IdentifierNameNode("void"),
                true
        );
    }
}
