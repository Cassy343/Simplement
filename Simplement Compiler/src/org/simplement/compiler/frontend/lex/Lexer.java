package org.simplement.compiler.frontend.lex;

import org.simplement.compiler.generic.CompilationException;
import org.simplement.compiler.generic.FailCode;
import org.simplement.compiler.generic.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Lexer {
    private final List<Pair<Token, Object>> tokens;
    private final File source;

    public Lexer(File source) {
        this.tokens = new ArrayList<>(512);
        this.source = source;
    }

    public void lex() {
        StringBuilder stringConst = new StringBuilder();
        char[] token = new char[64];
        int index = 0, lineCount = 1, stringStartLine = 0, num = 1;
        boolean isq = false, // In single quotes
                idq = false, // In double quotes
                slc = false, // Single-line commenting
                mlc = false; // Multi-line commenting

        try {
            InputStreamReader ifstream = new InputStreamReader(new FileInputStream(source), Charset.forName("UTF-8"));
            // Current char, next char, next-next char, token number type (0: NaN, 1: int, 2: float)
            int c, nc = ifstream.read();
            while(nc > -1) {
                c = nc;
                nc = ifstream.read();

                // Comments. Single line: #... Multi-line: #-...-#
                // New lines added to keep track of line count
                if(slc) {
                    if(c == '\n') {
                        slc = false;
                        ++ lineCount;
                        tokens.add(new Pair<>(Token.NEW_LINE, null));
                    }
                    continue;
                }else if(mlc) {
                    if(c == '\n') {
                        ++ lineCount;
                        tokens.add(new Pair<>(Token.NEW_LINE, null));
                    }else if(c == '-' && nc == '#') { // - character followed by #
                        mlc = false;
                        nc = ifstream.read();
                    }
                    continue;
                }

                // Handling string constants
                if(idq || isq) {
                    switch(c) {
                        case '\n': // Not allowed
                            throw new CompilationException(FailCode.INVALID_TOKEN, "Unmatched string starting on line " + lineCount + " in " + source.getName());
                        case '\\': // Escaped characters
                            stringConst.append('\\').appendCodePoint(nc);
                            nc = ifstream.read();
                            continue;
                        case '\"': // Potential end to a string (double quotes)
                            if(idq) {
                                idq = false;
                                num = 1;
                                addToken(tokens, lineCount, stringConst);
                            }else
                                stringConst.appendCodePoint(c);
                            break;
                        case '\'': // Potential end to a string (single quotes)
                            if(isq) {
                                isq = false;
                                num = 1;
                                addToken(tokens, lineCount, stringConst);
                            }else
                                stringConst.appendCodePoint(c);
                            break;
                        default: // Extending the string
                            stringConst.appendCodePoint(c);
                    }
                    continue;
                }else{
                    switch(c) {
                        case '#': // Start of a comment
                            // If the sequence is #- then it's a multi-line comment
                            if(nc == '-')
                                mlc = true;
                            else
                                slc = true;
                            continue;
                        case '\"': // Start of a string (double quotes)
                            stringStartLine = lineCount;
                            idq = true;
                            continue;
                        case '\'': // Start of a string (single quotes)
                            stringStartLine = lineCount;
                            isq = true;
                            continue;
                    }
                }

                // New-line handling
                if(c == '\n') {
                    if(index > 0) { // If there was a token in the works, parse it
                        addToken(tokens, token, index);
                        index = 0;
                    }
                    ++ lineCount;
                    // Add a new line token
                    tokens.add(new Pair<>(Token.NEW_LINE, null));
                    num = 1;
                    continue;
                }

                // Checks if we're parsing a number
                if((c < '0' || c > '9') && index == 0)
                    num = 0;

                // Non-word tokens
                if((c < '0' || c > '9') && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && c != '_') { // Is c not alphanumeric?
                    if(num == 0) {
                        if(index > 0) {
                            addToken(tokens, token, index);
                            index = 0;
                        }

                        if(c > ' ') { // If this token is not whitespace
                            // Is nc not alphanumeric and not whitespace? Then it's a double-char token
                            if((nc < '0' || nc > '9') && (nc < 'A' || nc > 'Z') && (nc < 'a' || nc > 'z') && nc != '_' && nc > ' ') {
                                addTokens(tokens, lineCount, (char)c, (char)nc);
                                nc = ifstream.read();
                            }else // Single-char token
                                addToken(tokens, lineCount, (char)c);
                        }
                        num = 1;
                        continue;
                    }else{
                        if(c == '.')
                            num = 2;
                        else{
                            String tokenString = new String(token, 0, index);
                            int idx = tokens.size() - 1;
                            // If this constant is negated
                            if(idx >= 0 && Token.NEGATE.equals(tokens.get(idx).getFirst())) {
                                tokenString = "-" + tokenString;
                                tokens.remove(idx);
                            }
                            try {
                                if(num == 1)
                                    tokens.add(new Pair<>(Token.INTEGER_CONST, Token.INTEGER_CONST.getParser().apply(tokenString)));
                                else
                                    tokens.add(new Pair<>(Token.FLOAT_CONST, Double.valueOf(tokenString)));
                            }catch(NumberFormatException ex) {
                                throw new CompilationException(FailCode.INVALID_TOKEN, "Invalid numeric literal on line " + lineCount + " in " +
                                        source.getName() + ": " + tokenString);
                            }
                            index = 0;

                            // Same as before
                            if(c > ' ') {
                                if((nc < '0' || nc > '9') && (nc < 'A' || nc > 'Z') && (nc < 'a' || nc > 'z') && nc != '_' && nc > ' ') {
                                    addTokens(tokens, lineCount, (char)c, (char)nc);
                                    nc = ifstream.read();
                                }else
                                    addToken(tokens, lineCount, (char)c);
                            }
                            num = 1;
                            continue;
                        }
                    }
                }

                if(index >= 64) {
                    throw new CompilationException(FailCode.INVALID_TOKEN, "Invalid token on line " + lineCount + " in " + source.getName() +
                            ". Maximum token length of 256.");
                }

                // Floating point scientific form. Ex: 2.34e-2, 0x1f.89p+3
                if(num == 2 && (c == 'e' || c == 'E' || c == 'p' || c == 'P') && (nc == '+' || nc == '-')) {
                    token[index++] = (char)c;
                    c = nc;
                    nc = ifstream.read();
                }

                // Building a multi-character token
                token[index++] = (char)c;
            }

            ifstream.close();
        }catch(IOException ioe) {
            throw new CompilationException(FailCode.IO_ERROR, "Failed to read " + source.getName() + " during tokenization.");
        }

        // Checking for non-terminated strings
        if(stringConst.length() > 0)
            throw new CompilationException(FailCode.INVALID_TOKEN, "Unmatched string starting on line " + stringStartLine + " in " + source.getName());

        // Add the last token if it exists
        if(index > 0) {
            if(num > 0) { // Number handling
                String tokenString = new String(token, 0, index);
                int idx = tokens.size() - 1;
                if(idx >= 0 && Token.NEGATE.equals(tokens.get(idx).getFirst())) {
                    tokenString = "-" + tokenString;
                    tokens.remove(idx);
                }
                try {
                    if(num == 1)
                        tokens.add(new Pair<>(Token.INTEGER_CONST, Token.INTEGER_CONST.getParser().apply(tokenString)));
                    else
                        tokens.add(new Pair<>(Token.FLOAT_CONST, Double.valueOf(tokenString)));
                }catch(NumberFormatException ex) {
                    throw new CompilationException(FailCode.INVALID_TOKEN, "Invalid numeric literal on line " + lineCount + " in " +
                            source.getName() + ": " + tokenString);
                }
            }else
                addToken(tokens, token, index);
        }

        // Add a final NEW_LINE token if it's not already there
        if(tokens.isEmpty() || !Token.NEW_LINE.equals(tokens.get(tokens.size() - 1).getFirst()))
            tokens.add(new Pair<>(Token.NEW_LINE, null));
    }

    public List<Pair<Token, Object>> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    private void addTokens(List<Pair<Token, Object>> tokens, int line, char first, char second) {
        Pair<Token, Object> tkn = Token.tokenizeCompositeSymbol(first, second);
        if(tkn == null) {
            addToken(tokens, line, first);
            addToken(tokens, line, second);
        }else
            tokens.add(tkn);
    }

    private void addToken(List<Pair<Token, Object>> tokens, int line, StringBuilder token) {
        String raw = token.toString();
        Object finishedString = Token.STRING_CONST.getParser().apply(raw);
        if(finishedString == null)
            throw new CompilationException(FailCode.INVALID_TOKEN, "Invalid string literal on line " + line +
                    ": \'" + raw + "\'. Illegal escape character (\\).");
        tokens.add(new Pair<>(Token.STRING_CONST, finishedString));
        token.setLength(0);
    }

    private void addToken(List<Pair<Token, Object>> tokens, int line, char token) {
        // Differentiate between unary or binary + and -
        if(token == '+' || token == '-') {
            Token prevToken = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1).getFirst();
            if((prevToken == null || !(Token.IDENTIFIER.equals(prevToken) || Token.STRING_CONST.equals(prevToken) ||
                    Token.INTEGER_CONST.equals(prevToken) || Token.FLOAT_CONST.equals(prevToken) ||
                    Token.PARENTHESIS_CLOSE.equals(prevToken) || Token.SUBSCRIPT_CLOSE.equals(prevToken)))) {
                if(token == '-') // Ignore the useless unary +
                    tokens.add(new Pair<>(Token.NEGATE, null));
                return;
            }
        }
        Pair<Token, Object> tkn = Token.tokenizeBaseSymbol(token);
        if(tkn == null)
            throw new CompilationException(FailCode.INVALID_TOKEN, "Invalid token on line " + line + " in " + source.getName() + ": " + token);
        tokens.add(tkn);
    }

    private void addToken(List<Pair<Token, Object>> tokens, char[] token, int index) {
        tokens.add(Token.tokenizeWord(token, index));
    }
}
