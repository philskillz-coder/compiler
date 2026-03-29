package compiler.visitors.eval;

import compiler.parser.ast.*;
import compiler.visitors.eval.values.literal.NullValue;
import compiler.visitors.eval.values.memory.Closure;

import java.util.Collections;

public class Print extends FunctionDecl {

    public Print() {
        super(
                new Type("void"),
                "print",
                Collections.singletonList(new VariableDecl(
                        new Type("string"),
                        "text"
                )),
                new VirtualBlockExpr() {
                    @Override
                    public EvalResult execute(Closure closure) {
                        System.out.println(closure.getValueParent("text").toString());
                        return EvalResult.returnValue(NullValue.getInstance());
                    }
                }
        );
    }
}
