package org.simplement.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BytecodeTool extends Tool {
    public static final String MARKER_START = "!BCT_MARKER_START";
    public static final String MARKER_END = "!BCT_MARKER_END";
    private static final Map<String, String> PARAMETER_ALIAS = new HashMap<>();

    static {
        PARAMETER_ALIAS.put("FA", "FILE_ADDRESS");
        PARAMETER_ALIAS.put("IA", "INTERNAL_ADDRESS");
        PARAMETER_ALIAS.put("ID", "ID");
        PARAMETER_ALIAS.put("I8", "BYTE");
        PARAMETER_ALIAS.put("I32", "INTEGER");
        PARAMETER_ALIAS.put("I64", "LONG");
        PARAMETER_ALIAS.put("I16", "SHORT");
        PARAMETER_ALIAS.put("F64", "DOUBLE");
        PARAMETER_ALIAS.put("F32", "FLOAT");
    }

    public BytecodeTool(String[] args) {
        super(args, 2, "<codeFile> <outFile>");
    }

    @Override
    public void run() {
        try {
            run0();
        }catch(IOException ioe) {
            System.out.println("Encountered an I/O error.");
            ioe.printStackTrace(System.out);
        }catch(Exception ex) {
            System.out.println("Encountered an unknown error.");
            ex.printStackTrace(System.out);
        }
    }

    private void run0() throws Exception {
        Scanner scanner = new Scanner(new File(args[0]));
        List<Bytecode> bytecodes = new ArrayList<>();
        String line;
        int lc = 0;
        while(scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            ++ lc;

            if(line.isEmpty() || line.startsWith("#"))
                continue;

            if(!line.matches("[a-zA-Z]\\w+( ((" + String.join("|", PARAMETER_ALIAS.keySet()) + ")( |$))+)?")) {
                System.out.println("Line " + lc + " is invalid. Does not match code format.");
                scanner.close();
                return;
            }

            bytecodes.add(new Bytecode(line.split(" ")));
        }
        scanner.close();

        if(bytecodes.size() > 256) {
            System.out.println("Failed to update bytecoes. Maximum amount exceeded: 256.");
            return;
        }

        File dest = new File(args[1]);
        scanner = new Scanner(dest);
        List<String> lines = new ArrayList<>();
        lc = 0;
        int spaces = 0, index = -1;
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            lines.add(line);
            ++ lc;
            if(line.trim().matches("// " + MARKER_START + ".+")) {
                index = lc;
                spaces = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
                boolean flag = true;
                while(scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if(line.trim().matches("// " + MARKER_END)) {
                        lines.add(line);
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    System.out.println("No end tool marker found in destination file.");
                    return;
                }
            }
        }
        scanner.close();
        if(index < 0) {
            System.out.println("No start tool marker found in destination file.");
            return;
        }
        StringBuilder space = new StringBuilder();
        for(int i = 0;i < spaces;++ i)
            space.append(" ");
        int format = args[1].endsWith("java") ? 0 : 1;

        PrintStream out = new PrintStream(new FileOutputStream(dest));
        for(int i = 0;i < index;++ i)
            out.println(lines.get(i));
        for(int i = 0;i < bytecodes.size();++ i) {
            out.print(space);
            out.print(bytecodes.get(i).toString(format, i));
            if(format == 0)
                out.println(i == bytecodes.size() - 1 ? ';' : ',');
            else
                out.println();
        }
        for(int i = index;i < lines.size();++ i)
            out.println(lines.get(i));
        out.close();

        System.out.println("Updated " + bytecodes.size() + " bytecode" + (bytecodes.size() == 1 ? "" : "s"));
    }

    static final class Bytecode {
        final String name;
        final String[] parameters;

        Bytecode(String[] args) {
            this.name = args[0].toLowerCase();
            int paramCount = args.length - 1;
            this.parameters = new String[paramCount];
            System.arraycopy(args, 1, this.parameters, 0, paramCount);
        }

        String toString(int format, int value) {
            if(format == 0)
                return name.toUpperCase() + (parameters.length > 0 ? "(" + String.join(", ", Stream.of(parameters)
                        .map(PARAMETER_ALIAS::get).collect(Collectors.toList())) + ")" : "");
            else
                return "#define " + name + "_ 0x" + Integer.toHexString(value) + (parameters.length > 0 ? " //" + String.join(",", parameters) : "");
        }
    }
}
