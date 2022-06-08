package org.simplement.compiler.frontend;

import org.simplement.compiler.frontend.parse.Parser;
import org.simplement.compiler.frontend.lex.Lexer;
import org.simplement.compiler.generic.CompilationException;
import org.simplement.compiler.generic.FailCode;
import org.simplement.compiler.main.Arguments;

import java.io.File;

public class SingleFileCompiler extends Thread {
    private final Arguments args;
    private final File source;

    public SingleFileCompiler(Arguments args, File source) {
        this.args = args;
        this.source = source;
    }

    @Override
    public void run() {
        if(!source.exists())
            throw new CompilationException(FailCode.IO_ERROR, "Invalid file: " + source.getPath());
        if(!source.canRead())
            throw new CompilationException(FailCode.IO_ERROR, "Cannot read file " + source.getPath());

        /* Frontend */

        /* Process the source file into individual tokens */
        Lexer lexer = new Lexer(source);
        lexer.lex();
        lexer.getTokens().forEach(System.out::println);

        /* Construct an AST for the source file */
        String root = source.getAbsolutePath().substring(args.getArgument("source").length());
        root = root.substring(root.indexOf(File.separator), root.lastIndexOf('.')).replaceAll("\\Q" + File.separator + "\\E", ".");
        Parser parser = new Parser(root, source, lexer.getTokens());
        parser.parse();
    }
}
