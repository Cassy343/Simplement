package org.simplement.compiler.frontend.parse.element;

import org.simplement.compiler.backend.Bytecode;
import org.simplement.compiler.backend.Encoder;
import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.AbstractSyntaxTree;
import org.simplement.compiler.generic.Pair;
import org.simplement.compiler.generic.Primitive;
import org.simplement.compiler.generic.Type;

import java.util.List;

public class Print extends Expression {
    public Print(List<Pair<Token, Object>> bundle, int line, AbstractSyntaxTree ast) {
        super(bundle, line, ast);
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public String check() {
        if(childrenCount != 1)
            return "One operand required";
        else if(children[0].getReturnType().primitive.ordinal() > Primitive.OBJECT.ordinal())
            return "Operand cannot be a void, function, or module.";
        return null;
    }

    @Override
    public void encode(Encoder encoder) {
        children[0].encode(encoder);
        encoder.write(Bytecode.PRINT);
    }
}
