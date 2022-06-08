package org.simplement.compiler.main;

import org.simplement.compiler.generic.FailCode;

public final class SimplementCompiler implements Runnable {
    private final Arguments arguments;

    private SimplementCompiler(Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        Arguments arguments = new Arguments.Builder("simplec")
                .addRequiredArgument("source")
                .addOption("verbose", "Tells the compiler to print a large amount of debug output.")
                .build();

        if(!arguments.parse(args)) {
            System.exit(FailCode.INVALID_ARGS.ordinal());
            return;
        }

        (new SimplementCompiler(arguments)).run();
    }
}
