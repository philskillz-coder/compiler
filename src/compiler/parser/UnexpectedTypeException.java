package compiler.parser;

public class UnexpectedTypeException extends RuntimeException {
    public UnexpectedTypeException(String message) {
        super(message);
    }
}
