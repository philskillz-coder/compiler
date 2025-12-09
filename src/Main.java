import compiler.parser.ast.*;
import compiler.lexer.Token;
import compiler.lexer.Tokenizer;
import compiler.parser.TreeBuilder;
import compiler.visitors.eval.ASTEvalVisitor;

import java.util.List;

public class Main {
    private final String CODE = ""+
//            "var int a = 10;";
//            "func int fib(int n) { if (n == 0) { return 0; } else if (n == 1) { return 1; } else { return fib(n - 1) + fib(n - 2); } } print(fib(21));";
//    "var int a = (var int b = 5) + 10; print(a); print(b)";
    "func int fib(int n) {\n" +
            "    if (n == 0) {\n" +
            "        return 0;\n" +
            "    }\n" +
            "\n" +
            "    var int a = 0;\n" +
            "    var int b = 1;\n" +
            "\n" +
            "    var int i = 1;\n" +
            "    while (i < n) {\n" +
            "        var int tmp = a + b;\n" +
            "        a = b;\n" +
            "        b = tmp;\n" +
            "        i = i + 1;\n" +
            "    }\n" +
            "\n" +
            "    return b;\n" +
            "}\n" +
            "\n" +
            "print(fib(17));";

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