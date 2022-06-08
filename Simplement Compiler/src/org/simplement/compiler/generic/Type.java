package org.simplement.compiler.generic;

import org.simplement.compiler.frontend.lex.Token;

public class Type {
    public static final Type VOID = new Type(Primitive.VOID);

    public final Primitive primitive;
    public final Object info;

    public Type(Primitive primitive, Object info) {
        this.primitive = primitive;
        this.info = info;
    }

    public Type(Primitive primitive) {
        this(primitive, null);
    }

    public static Type forLiteral(Token token) {
        switch(token) {
            case INTEGER_CONST: return Primitive.LONG.asType();
            case FLOAT_CONST: return Primitive.DOUBLE.asType();
            case STRING_CONST: return Primitive.STRING.asType();
            case BOOLEAN_CONST: return Primitive.BOOLEAN.asType();
            default: return null;
        }
    }
}
