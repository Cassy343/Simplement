package org.simplement.compiler.generic;

public enum Primitive {
    BYTE,
    UNSIGNED_BYTE,
    SHORT,
    UNSIGNED_SHORT,
    INTEGER,
    UNSIGNED_INTEGER,
    LONG,
    UNSIGNED_LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BOOLEAN,
    OBJECT,
    ARRAY,
    VOID,
    FUNCTION,
    MODULE;

    public static final Primitive[] VALUES = values();

    public Type asType() {
        return new Type(this);
    }
}
