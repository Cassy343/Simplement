package org.simplement.compiler.frontend.parse.element;

//import org.simplement.compiler.generic.ByteBuffer;
import org.simplement.compiler.backend.Encoder;
import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.AbstractSyntaxTree;
import org.simplement.compiler.generic.*;

import java.util.List;

public abstract class Expression {
    protected final List<Pair<Token, Object>> tokens;
    protected final AbstractSyntaxTree ast;
    protected final int line;
    protected final Expression[] children;
    protected int childrenCount;

    protected Expression(List<Pair<Token, Object>> tokens, int line, AbstractSyntaxTree ast) {
        this.tokens = tokens;
        this.ast = ast;
        this.line = line;
        this.children = new Expression[3];
        this.childrenCount = 0;
    }

    public void addChild(Expression child) {
        if(childrenCount >= 3 || child == null)
            return;
        this.children[childrenCount++] = child;
    }

    public abstract Type getReturnType();

    public abstract String check();

    public abstract void encode(Encoder encoder);
}
