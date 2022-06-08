package org.simplement.compiler.frontend.parse.element;

import static org.simplement.compiler.backend.Bytecode.*;

import org.simplement.compiler.backend.Encoder;
import org.simplement.compiler.frontend.lex.Token;
import org.simplement.compiler.frontend.parse.AbstractSyntaxTree;
import org.simplement.compiler.generic.*;

import java.util.List;

public class Constant extends Expression {
    private final Type type;
    private final Object value;

    public Constant(Type type, Object value, List<Pair<Token, Object>> tokens, int line, AbstractSyntaxTree ast) {
        super(tokens, line, ast);
        this.type = type;
        this.value = value;
    }

    @Override
    public Type getReturnType() {
        return type;
    }

    @Override
    public String check() {
        return null;
    }

    @Override
    public void encode(Encoder encoder) {
        if(type.primitive.ordinal() < 8) {
            long v = (long)value;
            if(v == -1L)
                encoder.write(ICONST_N1);
            else if(v == 0L)
                encoder.write(ICONST_0);
            else if(v == 1L)
                encoder.write(ICONST_1);
            else if(v == 2L)
                encoder.write(ICONST_2);
            else if(v == 3L)
                encoder.write(ICONST_3);
            else if(v == 4L)
                encoder.write(ICONST_4);
            else if(v == 5L)
                encoder.write(ICONST_5);
            else{
                switch(type.primitive) {
                    case INTEGER: encoder.write(ICONST, value); break;
                    case LONG: encoder.write(LCONST, value); break;
                    case SHORT: encoder.write(JCONST, value); break;
                    case BYTE: encoder.write(BCONST, value); break;
                }
            }
        }else if(type.primitive.ordinal() < 10) {
            boolean isDouble = Primitive.DOUBLE.equals(type.primitive);
            double d = (double)value;
            if(d == 0.0)
                encoder.write(isDouble ? DCONST_0 : FCONST_0);
            else if(d == 1.0)
                encoder.write(isDouble ? DCONST_1 : FCONST_1);
            else if(d == -1.0)
                encoder.write(isDouble ? DCONST_N1 : FCONST_N1);
            else
                encoder.write(isDouble ? DCONST : FCONST, value);
        }else{
            if(Primitive.STRING.equals(type.primitive))
                encoder.write(SCONST, value);
            else
                encoder.write((boolean)value ? ZTRUE : ZFALSE);
        }
    }
}
