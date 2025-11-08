import compiler.ast.*;
import compiler.lexer.Token;
import compiler.lexer.Tokenizer;
import compiler.parser.TreeBuilder;
import compiler.visitors.eval.ASTEvalVisitor;

import java.util.List;

public class Main {
    private final String CODE = ""+
//            "func int fib(int n) { if (n == 0) { return 0; } else if (n == 1) { return 1; } else { return fib(n - 1) + fib(n - 2); } } print(fib(3));";
        "func void t(int n) {print(n);} t(1);";
    // func

    public Main() {
        Tokenizer t = new Tokenizer(CODE);
        List<Token<?>> tokens = t.tokenize();
        System.out.println(tokens);
        TreeBuilder tb = new TreeBuilder(tokens);
        ProgramNode p = tb.parse();
        p.accept(new ASTEvalVisitor());
    }

    public static void main(String[] args) {
        new Main();
    }
}