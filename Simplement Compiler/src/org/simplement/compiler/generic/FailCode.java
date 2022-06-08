package org.simplement.compiler.generic;

public enum FailCode {
    INVALID_ARGS,
    INVALID_TOKEN,
    IO_ERROR,
    UNKNOWN;

    public void fail() {
        int ordinal = ordinal();
        System.out.println("BUILD FAILED. Error Code: " + ordinal + " (" + name() + ")");
        System.exit(ordinal);
    }
}
