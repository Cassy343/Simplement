package org.simplement.compiler.frontend.parse.element;

import org.simplement.compiler.backend.Bytecode;
import org.simplement.compiler.backend.Encoder;
import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.AbstractSyntaxTree;
import org.simplement.compiler.generic.Pair;
import org.simplement.compiler.generic.Primitive;
import org.simplement.compiler.generic.Type;

import java.util.List;

public class Addition extends Expression {
    private Type returnType;

    public Addition(List<Pair<Token, Object>> tokens, int line, AbstractSyntaxTree ast) {
        super(tokens, line, ast);
        this.returnType = null;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String check() {
        if(childrenCount != 2)
            return "Two operands required.";
        String a = children[0].check(), b = children[1].check();
        if(a != null || b != null)
            return (a == null ? "" : a + "\n") + (b == null ? "" : b);
        returnType = Primitive.VALUES[Math.max(children[0].getReturnType().primitive.ordinal(), children[1].getReturnType().primitive.ordinal())].asType();
        if(returnType.primitive.ordinal() > Primitive.STRING.ordinal())
            return "Both operands must be numeric or strings.";
        return null;
    }

    @Override
    public void encode(Encoder encoder) {
        children[0].encode(encoder);
        children[1].encode(encoder);
        encoder.write(Bytecode.forPrimitive(Bytecode.ADD8, returnType.primitive));
    }
}
