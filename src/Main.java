import compiler.parser.ast.*;
import compiler.lexer.Token;
import compiler.lexer.Tokenizer;
import compiler.parser.TreeBuilder;
import compiler.visitors.eval.ASTEvalVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    /*private final String CODE = ""+
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
            "print(fib(17));"; */

    public Main() {
        // read file "code.src"
        String code;
        System.out.println("Ich suche hier: " + System.getProperty("user.dir"));
        try {
            code = Files.readString(Paths.get("src/code.src"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Tokenizer t = new Tokenizer(code);
        List<Token<?>> tokens = t.tokenize();
        System.out.println(tokens);
        TreeBuilder tb = new TreeBuilder(tokens);
        Program p = tb.parse();
        p.accept(new ASTEvalVisitor());
    }

    public static void main(String[] args) {
        new Main();
    }
}