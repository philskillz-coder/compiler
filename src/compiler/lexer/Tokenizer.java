package compiler.lexer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final String input;
    private final List<Token<?>> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token<?>> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(0, new TokenSOF());
        tokens.add(new TokenEOF());

        return tokens;
    }

    private boolean isAtEnd() {
        return current >= input.length();
    }

    private void scanToken() {
        char ch = advance(); // Konsumiert das nächste Zeichen

        switch (ch) {
            // --- Ein-Zeichen-Trennzeichen ---
            case '(': tokens.add(new TokenParenOpen()); break;
            case ')': tokens.add(new TokenParenClose()); break;
            case '{': tokens.add(new TokenCurlyOpen()); break;
            case '}': tokens.add(new TokenCurlyClose()); break;
            case ';': tokens.add(new TokenSemicolon()); break;
            case ',': tokens.add(new TokenComma()); break;
            case '.': tokens.add(new TokenPeriod()); break;
            case '%': tokens.add(new TokenOperatorModulo()); break;
            case '~': tokens.add(new TokenOperatorBitwiseNot()); break; // Unäres Bit-NICHT
            case '^': tokens.add(new TokenOperatorBitwiseXor()); break; // Bitweises XOR

            // --- Multi-Zeichen-Operatoren mit Lookahead ---

            // + (Addition vs. Inkrement)
            case '+':
                if (match('+')) {
                    tokens.add(new TokenOperatorIncrement()); // ++
                } else {
                    tokens.add(new TokenOperatorAdd());      // +
                }
                break;

            // - (Subtraktion vs. Dekrement)
            case '-':
                if (match('-')) {
                    tokens.add(new TokenOperatorDecrement()); // --
                } else {
                    tokens.add(new TokenOperatorSub());       // - (auch unäre Negation)
                }
                break;

            // * (Multiplikation vs. Potenz)
            case '*':
                if (match('*')) {
                    tokens.add(new TokenOperatorPower());    // **
                } else {
                    tokens.add(new TokenOperatorMul());       // *
                }
                break;

            case '/': tokens.add(new TokenOperatorDiv()); break; // Später Kommentare behandeln

            // = (Gleichheit vs. Zuweisung)
            case '=':
                if (match('=')) {
                    tokens.add(new TokenOperatorEqual());    // ==
                } else {
                    tokens.add(new TokenOperatorAssign());   // =
                }
                break;

            // ! (Logisches NICHT vs. Ungleichheit)
            case '!':
                if (match('=')) {
                    tokens.add(new TokenOperatorNotEqual()); // !=
                } else {
                    tokens.add(new TokenOperatorLogicalNot()); // !
                }
                break;

            // < (Vergleich vs. KleinerGleich vs. Links-Shift)
            case '<':
                if (match('=')) {
                    tokens.add(new TokenOperatorLessEqual()); // <=
                } else if (match('<')) {
                    tokens.add(new TokenOperatorLeftShift()); // <<
                } else {
                    tokens.add(new TokenOperatorLess());     // <
                }
                break;

            // > (Vergleich vs. GrößerGleich vs. Rechts-Shift)
            case '>':
                if (match('=')) {
                    tokens.add(new TokenOperatorGreaterEqual()); // >=
                } else if (match('>')) {
                    tokens.add(new TokenOperatorRightShift()); // >>
                } else {
                    tokens.add(new TokenOperatorGreater());  // >
                }
                break;

            // & (Bitweises UND vs. Logisches UND)
            case '&':
                if (match('&')) {
                    tokens.add(new TokenOperatorLogicalAnd()); // &&
                } else {
                    tokens.add(new TokenOperatorBitwiseAnd()); // &
                }
                break;

            // | (Bitweises ODER vs. Logisches ODER)
            case '|':
                if (match('|')) {
                    tokens.add(new TokenOperatorLogicalOr());  // ||
                } else {
                    tokens.add(new TokenOperatorBitwiseOr());  // |
                }
                break;

            // --- Whitespace ignorieren ---
            case ' ':
            case '\r':
            case '\t':
            case '\n':
                // Ignoriere Whitespace; gehe zum nächsten Zeichen
                break;

            // --- String-Literale ---
            case '"':
                scanString(); // Ruft eine dedizierte Funktion für Strings auf
                break;

            // --- Zahlen und Identifier ---
            default:
                if (isDigit(ch)) {
                    scanNumber(); // Ruft eine dedizierte Funktion für Zahlen auf
                } else if (isAlpha(ch)) {
                    scanIdentifier(); // Ruft eine dedizierte Funktion für Identifier/Keywords auf
                } else {
                    // Behandelt ungültige Zeichen
                    System.err.println("Unerwartetes Zeichen: " + ch + " bei Index " + (current - 1));
                }
                break;
        }
    }

    // Konsumiert das aktuelle Zeichen und bewegt den Zeiger vor
    private char advance() {
        return input.charAt(current++);
    }

    // Sieht das nächste Zeichen an, ohne den Zeiger zu bewegen
    private char peek() {
        if (isAtEnd()) return '\0'; // End of File
        return input.charAt(current);
    }

    // Sieht zwei Zeichen voraus (für Operatoren wie '!=', '==')
    private char peekNext() {
        if (current + 1 >= input.length()) return '\0';
        return input.charAt(current + 1);
    }

    // Bedingter Konsum: Prüft, ob das nächste Zeichen 'expected' ist,
    // konsumiert es und gibt true zurück, andernfalls false.
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (input.charAt(current) != expected) return false;

        current++; // Konsumiert das Zeichen
        return true;
    }

    // --- Detail-Handler für komplexe Tokens ---

    private void scanString() {
        // Solange das Ende der Eingabe oder das abschließende " nicht erreicht ist
        while (peek() != '"' && !isAtEnd()) {
            // Behandle z.B. Newline-Zeichen oder Escape-Sequenzen
            advance();
        }

        if (isAtEnd()) {
            System.err.println("Nicht abgeschlossenes String-Literal.");
            return;
        }

        // Schließendes " konsumieren
        advance();

        // Der String-Wert ist alles zwischen den Anführungszeichen
        String value = input.substring(start + 1, current - 1);
        tokens.add(new TokenLiteralString(value));
    }

    private void scanNumber() {
        // Solange das nächste Zeichen eine Ziffer ist, bewegen wir uns vor
        while (isDigit(peek())) {
            advance();
        }

        // Behandle Dezimalpunkte (float/double)
        if (peek() == '.' && isDigit(peekNext())) {
            // Konsumiere den Dezimalpunkt
            advance();

            // Konsumiere die Ziffern nach dem Punkt
            while (isDigit(peek())) {
                advance();
            }
        }

        // Extrahiere den Wert und entscheide über den Token-Typ
        String value = input.substring(start, current);
        if (value.contains(".")) {
            tokens.add(new TokenLiteralFloat(Double.parseDouble(value)));
        } else {
            tokens.add(new TokenLiteralInt(Integer.parseInt(value)));
        }
    }

    private void scanIdentifier() {
        // Solange das nächste Zeichen ein Buchstabe, eine Ziffer oder '_' ist
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // Extrahiere den potenziellen Identifier
        String text = input.substring(start, current);

        // Prüfe, ob es ein Schlüsselwort ist (hier müsste Ihre Keyword-Logik rein)
        switch (text) {
            case "var": tokens.add(new TokenKwVar()); break;
            case "if": tokens.add(new TokenKwIf()); break;
            case "else": tokens.add(new TokenKwElse()); break;
            case "while": tokens.add(new TokenKwWhile()); break;
            case "func": tokens.add(new TokenKwFnDecl()); break;
            case "return": tokens.add(new TokenKwReturn()); break;
            case "result": tokens.add(new TokenKwResult()); break;

            default: tokens.add(new TokenIdentifier(text)); break;
        }
    }

    // Hilfsfunktionen zur Zeichenklassifizierung (müssen Sie selbst implementieren oder nutzen)
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}