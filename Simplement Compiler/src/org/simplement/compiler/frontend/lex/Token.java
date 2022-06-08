package org.simplement.compiler.frontend.lex;

import static org.simplement.compiler.frontend.lex.TokenCategory.*;

import org.simplement.compiler.frontend.parse.Operator;
import org.simplement.compiler.generic.Pair;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Token {
    /* Keywords */
    AS("as", WORD),
    CLASS_DEF("class", WORD, Operator.CLASS_DEF),
    ELSE("else", WORD),
    FOR("for", WORD, Operator.FOR),
    FUNC_DEF("func", WORD, Operator.FUNC_DEF),
    IF("if", WORD, Operator.IF),
    IMPORT("import", WORD, Operator.IMPORT),
    IN("in", WORD),
    PRINT("print", WORD, Operator.PRINT),
    PRINTLN("println", WORD, Operator.PRINTLN),
    PRIVATE_MOD("private", WORD),
    PROTECTED_MOD("protected", WORD),
    PUBLIC_MOD("public", WORD),
    RETURN("return", WORD, Operator.RETURN),
    STATIC("static", WORD),
    WHILE("while", WORD, Operator.WHILE),

    /* Operators */
    ACCESS(".", BASE_SYMBOL, Operator.OBJECT_ACCESS),
    ADD("+", BASE_SYMBOL, Operator.ADDITION),
    ASSIGNMENT("=", BASE_SYMBOL, Operator.ASSIGN),
    BIT_AND("&", BASE_SYMBOL, Operator.BIT_AND),
    BIT_COMPLEMENT("~", BASE_SYMBOL, Operator.BIT_COMPLEMENT),
    BIT_OR("|", BASE_SYMBOL, Operator.BIT_OR),
    BIT_XOR("^", BASE_SYMBOL, Operator.BIT_XOR),
    BLOCK_OPEN("{", BASE_SYMBOL),
    BLOCK_CLOSE("}", BASE_SYMBOL),
    COLON(":", BASE_SYMBOL),
    DIVIDE("/", BASE_SYMBOL, Operator.DIVIDE),
    GREATER_THAN(">", BASE_SYMBOL, Operator.GREATER_THAN),
    LESS_THAN("<", BASE_SYMBOL, Operator.LESS_THAN),
    MODULUS("%", BASE_SYMBOL, Operator.MODULUS),
    MULTIPLY("*", BASE_SYMBOL, Operator.MULTIPLY),
    NOT("!", BASE_SYMBOL, Operator.LOGIC_NOT),
    PARENTHESIS_CLOSE(")", BASE_SYMBOL),
    PARENTHESIS_OPEN("(", BASE_SYMBOL, Operator.CALL),
    SEPARATOR(",", BASE_SYMBOL),
    NEW_LINE("\n", null), // Handled by the lexer
    SUBTRACT("-", BASE_SYMBOL, Operator.SUBTRACTION),
    SUBSCRIPT_OPEN("[", BASE_SYMBOL, Operator.ARRAY_ACCESS),
    SUBSCRIPT_CLOSE("]", BASE_SYMBOL),
    TERNARY("?", BASE_SYMBOL, Operator.TERNARY),
    NEGATE("-", null, Operator.NEGATE), // Handled by the lexer

    /* Composite Operators */
    AND("&&", COMPOSITE_SYMBOL, Operator.LOGIC_AND),
    BIT_LSHIFT("<<", COMPOSITE_SYMBOL, Operator.BIT_LSHIFT),
    BIT_RSHIFT(">>", COMPOSITE_SYMBOL, Operator.BIT_RSHIFT),
    BIT_URSHIFT("~>", COMPOSITE_SYMBOL, Operator.BIT_URSHIFT),
    DECREMENT("--", COMPOSITE_SYMBOL, Operator.DECREMENT),
    EQUALS("==", COMPOSITE_SYMBOL, Operator.EQUAL),
    GREATER_THAN_OR_EQU(">=", COMPOSITE_SYMBOL, Operator.GREATER_THAN_OR_EQU),
    INCREMENT("++", COMPOSITE_SYMBOL, Operator.INCREMENT),
    LESS_THAN_OR_EQU("<=", COMPOSITE_SYMBOL, Operator.LESS_THAN_OR_EQU),
    NOT_EQUALS("!=", COMPOSITE_SYMBOL, Operator.NOT_EQUAL),
    OR("||", COMPOSITE_SYMBOL, Operator.LOGIC_OR),

    /* Identifiers and Constants (Special Cases) */
    IDENTIFIER(TokenCategory.IDENTIFIER, null),
    STRING_CONST(LITERAL, string -> {
        if(string.indexOf('\\') > -1) { // Handle escape characters
            StringBuilder sb = new StringBuilder();
            char c;
            int end = string.lastIndexOf('\\') + 1, len = string.length();
            for(int i = string.indexOf('\\');i < end;++ i) {
                c = string.charAt(i);
                if(c == '\\') {
                    char nextChar = string.charAt(i + 1);
                    switch(nextChar) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'b': sb.append('\b'); break;
                        case 'r': sb.append('\r'); break;
                        case '0': sb.appendCodePoint(0); break;
                        case 'f': sb.append('\f'); break;
                        case '\"':
                        case '\'':
                        case '\\':
                            sb.append(nextChar);
                            break;
                        case 'u':
                        {
                            if(len < i + 6) // Make sure we have a 4-digit code
                                return null;
                            int cp;
                            try { // Try to parse the hex code
                                cp = Integer.parseInt(string.substring(i + 2, i + 6), 16);
                            }catch(NumberFormatException ex) {
                                return null;
                            }
                            sb.appendCodePoint(cp);
                            i += 5;
                            continue;
                        }
                        default: return null;
                    }
                    ++ i;
                }else
                    sb.append(c);
            }
            return sb.toString();
        }
        return string;
    }),
    BOOLEAN_CONST(LITERAL, null),
    INTEGER_CONST(LITERAL, string -> {
        String str;
        boolean negate = false;
        if(string.charAt(0) < '0') {
            str = string.substring(1);
            negate = true;
        }else
            str = string;
        long l;
        if(str.length() == 1 || str.charAt(0) > '0') // Base 10
            l = Long.parseLong(str, 10);
        else if(str.charAt(1) == 'x') // Hexadecimal
            l = Long.parseLong(str.substring(2), 16);
        else if(str.charAt(1) == 'b') // Binary
            l = Long.parseLong(str.substring(2), 2);
        else // Octal
            l = Long.parseLong(str.substring(1), 8);
        return negate ? -l : l;
    }),
    FLOAT_CONST(LITERAL, null),
    NULL("null", LITERAL);

    private final char[] matcher;
    private final TokenCategory category;
    private final Operator operator;
    private final Function<String, Object> parser;

    public static final Token[] VALUES = values();
    private static final Token[] BASE_SYMBOLS = Arrays.stream(VALUES).filter(Token::isBaseSymbol)
            .collect(Collectors.toList()).toArray(new Token[0]);
    private static final Token[] COMPOSITE_SYMBOLS = Arrays.stream(VALUES).filter(Token::isCompositeSymbol)
            .collect(Collectors.toList()).toArray(new Token[0]);
    private static final Token[] WORDS = Arrays.stream(VALUES).filter(Token::isWord)
            .collect(Collectors.toList()).toArray(new Token[0]);

    Token(String matcher, TokenCategory category, Operator operator, Function<String, Object> parser) {
        this.matcher = matcher == null ? null : matcher.toCharArray();
        this.parser = parser;
        this.category = category;
        this.operator = operator;
    }

    Token(TokenCategory category, Function<String, Object> parser) {
        this(null, category, null, parser);
    }

    Token(String matcher, TokenCategory category, Operator operator) {
        this(matcher, category, operator, null);
    }

    Token(String matcher, TokenCategory category) {
        this(matcher, category, null, null);
    }

    public boolean isBaseSymbol() {
        return BASE_SYMBOL.equals(category);
    }

    public boolean isCompositeSymbol() {
        return COMPOSITE_SYMBOL.equals(category);
    }

    public boolean isWord() {
        return WORD.equals(category);
    }

    public boolean isLiteral() {
        return LITERAL.equals(category);
    }

    public Operator getOperator() {
        return operator;
    }

    public Function<String, Object> getParser() {
        return parser;
    }

    public static Pair<Token, Object> tokenizeBaseSymbol(char c) {
        for(Token type : BASE_SYMBOLS) {
            if(type.matcher[0] == c)
                return new Pair<>(type, null);
        }
        return null;
    }

    public static Pair<Token, Object> tokenizeCompositeSymbol(char first, char second) {
        for(Token type : COMPOSITE_SYMBOLS) {
            if(type.matcher[0] == first && type.matcher[1] == second)
                return new Pair<>(type, null);
        }
        return null;
    }

    public static Pair<Token, Object> tokenizeWord(char[] token, int length) {
        outer:
        for(Token type : WORDS) {
            if(type.matcher.length == length) {
                for(int i = 0;i < length;++ i) {
                    if(type.matcher[i] != token[i])
                        continue outer;
                }
                return new Pair<>(type, null);
            }
        }

        if(length == 4 && token[0] == 'n' && token[1] == 'u' && token[2] == 'l' && token[3] == 'l')
            return new Pair<>(NULL, null);
        else if(length == 4 && token[0] == 't' && token[1] == 'r' && token[2] == 'u' && token[3] == 'e')
            return new Pair<>(BOOLEAN_CONST, true);
        else if(length == 5 && token[0] == 'f' && token[1] == 'a' && token[2] == 'l' && token[3] == 's' && token[4] == 'e')
            return new Pair<>(BOOLEAN_CONST, false);
        else
            return new Pair<>(IDENTIFIER, new String(token, 0, length));
    }
}
