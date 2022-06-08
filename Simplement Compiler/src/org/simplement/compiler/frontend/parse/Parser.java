package org.simplement.compiler.frontend.parse;

import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.element.*;
import org.simplement.compiler.generic.CompilationException;
import org.simplement.compiler.generic.Pair;
import org.simplement.compiler.generic.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private final List<Pair<Token, Object>> tokens;
    private int currentLine;
    private int currentTokenIndex;
    private final File source;
    private final List<CompilationException> exceptions;
    private final AbstractSyntaxTree ast;

    public Parser(String astRoot, File source, List<Pair<Token, Object>> tokens) {
        this.tokens = tokens;
        this.currentLine = 1;
        this.currentTokenIndex = 0;
        this.source = source;
        this.exceptions = new ArrayList<>();
        this.ast = new AbstractSyntaxTree(astRoot);
    }

    public void parse() {
        parseBlock(ast.getModule());
    }

    private void parseBlock(CodeBlock block) {
        int len = tokens.size(), start = -1, end = -1;
        Pair<Token, Object> token;
        while(currentTokenIndex < len && !Token.BLOCK_CLOSE.equals(tokens.get(currentTokenIndex).getFirst())) {
            token = tokens.get(currentTokenIndex);
            if(start == end) {
                if(Token.NEW_LINE.equals(token.getFirst())) {
                    ++ currentLine;
                    ++ currentTokenIndex;
                    continue;
                }else{
                    start = currentTokenIndex;
                    end = currentTokenIndex + 1;
                }
            }
            if(Token.NEW_LINE.equals(token.getFirst())) {
                ++ currentLine;
                end = currentTokenIndex;
            }else{
                ++ currentTokenIndex;
                continue;
            }
            try {
                Expression exp = parseExpression(block, start, end);
                if(exp != null)
                    block.appendExpression(exp);
            }catch(CompilationException ex) {
                exceptions.add(ex);
            }
        }
    }

    private Expression parseExpression(CodeBlock block, int start, int end) {
        Operator lowestPrecedence = null;
        int index = start, right = end;
        for(int i = start;i < end;++ i) {
            Operator current = tokens.get(i).getFirst().getOperator();
            if(current != null) {
                if(lowestPrecedence == null || current.getPrecedence() < lowestPrecedence.getPrecedence()) {
                    lowestPrecedence = current;
                    index = right = i;
                    continue;
                }
                if(current.equals(lowestPrecedence))
                    right = i;
            }
        }
        if(lowestPrecedence == null) {
            if(end - start == 1) {
                Pair<Token, Object> token = tokens.get(index);
                if(Token.IDENTIFIER.equals(token.getFirst())) {
                    String name = (String)token.getSecond();
                    Identifier id = ast.getIdentifier((String)token.getSecond());
                    if(id == null) {
                        id = new ImportedIdentifier(name);
                        ast.putImportedIdentifier((ImportedIdentifier)id);
                    }
                    return id;
                }else if(token.getFirst().isLiteral())
                    return new Constant(Type.forLiteral(token.getFirst()), token.getSecond(), tokens, currentLine, ast);
                else
                    throw new CompilationException("Expected identifier or literal as operand on line " + currentLine);
            }else
                throw new CompilationException("Expected an operator on line " + currentLine);
        }
        index = lowestPrecedence.isLeftToRight() ? right : index;
        switch(lowestPrecedence) {

        }
        return null;
    }
}
