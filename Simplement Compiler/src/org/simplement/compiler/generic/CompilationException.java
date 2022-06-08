package org.simplement.compiler.generic;

public final class CompilationException extends RuntimeException {
    private final FailCode failCode;

    public CompilationException(FailCode failCode, String msg) {
        super(msg);
        this.failCode = failCode;
    }

    public CompilationException(String msg, Exception cause) {
        super(msg, cause);
        this.failCode = FailCode.UNKNOWN;
    }

    public CompilationException(String msg) {
        this(FailCode.UNKNOWN, msg);
    }

    public void complete() {
        System.out.println("Compilation error: " + getMessage());
        if(FailCode.UNKNOWN.equals(failCode) && getCause() != null) {
            System.out.println("Caused by:");
            getCause().printStackTrace(System.out);
        }
        failCode.fail();
    }
}
