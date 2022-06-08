package org.simplement.tools;

import java.io.*;
import java.util.*;

public class AssemblerTool extends Tool {
    public static final String BCP_MARKER_START = "!AST_MARKER_START_BCP";
    public static final String TYPES_MARKER_START = "!AST_MARKER_START_TYPES";
    public static final String NATIVES_MARKER_START = "!AST_MARKER_START_NATIVES";
    public static final String MARKER_END = "!AST_MARKER_END";

    public AssemblerTool(String[] args) {
        super(args, 1, "<codeFile> [outFile]");
    }

    @Override
    public void run() {
        try {
            run0();
        }catch(AssemblyException ex) {
            System.out.println("Encountered and error during assembly:\n" + ex.getMessage());
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
        if(!scanner.hasNextLine()) {
            scanner.close();
            throw new AssemblyException("The source file is empty.");
        }

        String line = scanner.nextLine();
        if(!line.startsWith("%%")) {
            scanner.close();
            throw new AssemblyException("The first line must start with %%<file> where <file> is the spldefs.h that should be used for formatting.");
        }
        Format format = configure(line.substring(2).split("\\|"));

        int len = 0;
        List<Command> commands = new ArrayList<>();
        Map<String, Integer> vars = new HashMap<>();
        while(scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if(line.isEmpty())
                continue;
            if(line.startsWith("#"))
                continue;
            if(line.contains("#"))
                line = line.substring(0, line.indexOf('#')).trim();
            int idx = line.indexOf(' ');
            String command = line.substring(0, idx < 0 ? line.length() : idx);
            boolean isAsCmd = command.charAt(0) == '%';
            if(isAsCmd)
                command = command.substring(1);
            String[] args;
            if("utf8".equals(command)) {
                args = new String[3];
                args[0] = line.substring(idx + 1, idx + 3);
                args[1] = line.substring(idx + 4, idx + 5);
                args[2] = line.substring(idx + 6);
            }else
                args = idx < 0 ? null : line.substring(idx + 1).split(" ");
            if(isAsCmd) {
                if("setfa".equals(command)) {
                    vars.put(args[0], len);
                    continue;
                }else if("set".equals(command)) {
                    vars.put(args[0], Integer.parseInt(args[1]));
                    continue;
                }
            }
            len += format.sizeof(command, args);
            commands.add(new Command(isAsCmd, command, args));
        }
        scanner.close();

        OutputStream out = new FileOutputStream(new File(args[1]));
        for(Command cmd : commands) {
            if("b".equals(cmd.command)) {
                for(String arg : cmd.args)
                    out.write(Integer.parseInt(arg, 16));
            }else if("f32".equals(cmd.command)) {
                for(String arg : cmd.args)
                    format.writef32(Float.parseFloat(arg), out);
            }else if("f64".equals(cmd.command)) {
                for(String arg : cmd.args)
                    format.writef64(Double.parseDouble(arg), out);
            }else if("utf8".equals(cmd.command))
                format.writeUtf8("ul".equals(cmd.args[0]), Integer.parseInt(cmd.args[1]), cmd.args[2], out);
            else if("type".equals(cmd.command)) {
                long[] args;
                String[] splitarg;
                for(String arg : cmd.args) {
                    splitarg = arg.split(":");
                    args = new long[splitarg.length - 1];
                    for(int i = 0;i < args.length;++ i)
                        args[i] = eval(splitarg[i + 1], format, vars);
                    format.writeType(splitarg[0], args, out);
                }
            }else{
                long[] args = null;
                if(cmd.args != null) {
                    args = new long[cmd.args.length];
                    for(int i = 0;i < args.length;++ i)
                        args[i] = eval(cmd.args[i], format, vars);
                }
                if(cmd.isAsCmd) {
                    for(long arg : args)
                        format.writei(cmd.command, arg, out);
                }else
                    format.writeBytecode(cmd.command, args, out);
            }
        }
        out.close();
    }

    private long eval(String arg, Format format, Map<String, Integer> vars) {
        if(arg.charAt(0) == '$') {
            String arg0 = arg.substring(1);
            int idx = arg0.indexOf('?');
            if(idx > -1) {
                String evalType = arg0.substring(0, idx), expr = arg0.substring(idx + 1);
                if("nfunc".equals(evalType)) {
                    Integer value = format.natives.get(expr);
                    if(value == null)
                        throw new AssemblyException("Invalid native function: " + expr);
                    return value.longValue();
                }else
                    throw new AssemblyException("Invalid eval type: " + evalType);
            }
            return vars.get(arg0);
        }else
            return Long.parseLong(arg);
    }

    private Format configure(String[] files) {
        if(files.length == 0 || !files[0].startsWith("config?"))
            throw new AssemblyException("The first argument in the file configuration must point to a spldefs.h and begin with \"config?\"");
        Scanner scanner;
        try {
            scanner = new Scanner(new File(files[0].substring(7)));
        }catch(FileNotFoundException ex) {
            throw new AssemblyException("File not found: " + files[0].substring(7));
        }
        Format format = new Format();
        readMarkerLines(BCP_MARKER_START, MARKER_END, scanner).forEach(line -> {
            line = line.substring(8);
            if(line.contains("/"))
                line = line.substring(0, line.indexOf('/'));
            line = line.trim();
            format.sizes.put(line.substring(1, line.indexOf('_')), Integer.parseInt(line.substring(line.indexOf(' ') + 5, line.lastIndexOf('_'))) / 8);
        });
        readMarkerLines(TYPES_MARKER_START, MARKER_END, scanner).forEach(line -> {
            line = line.substring(8);
            if(line.contains("/"))
                line = line.substring(0, line.indexOf('/'));
            line = line.trim();
            format.types.put(line.substring(1, line.indexOf('_')), Integer.parseInt(line.substring(line.indexOf(' ') + 1)));
        });
        readMarkerLines(BytecodeTool.MARKER_START, BytecodeTool.MARKER_END, scanner).forEach(line -> {
            String[] code = line.substring(8).trim().split(" ");
            format.putBytecode(code[0].substring(0, code[0].lastIndexOf('_')), (byte)Integer.parseInt(code[1].substring(2), 16),
                    code.length >= 3 ? code[2].substring(2).toLowerCase().split(",") : null);
        });
        format.calcTypeSize();
        scanner.close();
        for(int i = 1;i < files.length;++ i) {
            if(files[i].startsWith("natives?")) {
                try {
                    scanner = new Scanner(new File(files[i].substring(8)));
                }catch(FileNotFoundException ex) {
                    throw new AssemblyException("File not found: " + files[i].substring(8));
                }
                String[] funcs = String.join("", readMarkerLines(NATIVES_MARKER_START, MARKER_END, scanner)).split(",");
                for(int j = 0;j < funcs.length;++ j)
                    format.natives.put(funcs[j].trim(), j);
            }
        }
        return format;
    }

    static byte[] utf8Encoded(String string) {
        byte[] utf8;
        try {
            utf8 = string.getBytes("UTF-8");
        }catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        return utf8;
    }

    static class Format {
        final Map<String, Integer> sizes;
        final Map<String, Integer> types;
        final Map<String, Integer> natives;
        final Map<String, Bytecode> bytecodes;
        int typeSize;

        Format() {
            this.sizes = new HashMap<>();
            this.types = new HashMap<>();
            this.natives = new HashMap<>();
            this.bytecodes = new HashMap<>();
            this.typeSize = 0;
            initSizes();
        }

        void initSizes() {
            sizes.put("i8", 1);
            sizes.put("i16", 2);
            sizes.put("i32", 4);
            sizes.put("i64", 8);
            sizes.put("f32", 4);
            sizes.put("f64", 8);
            sizes.put("b", 1);
            sizes.put("bool", 1);
        }

        void calcTypeSize() {
            typeSize = 1 + Math.max(sizeof("id", null), 2);
        }

        int sizeof(String command, String[] args) {
            if(sizes.containsKey(command))
                return sizes.get(command) * (args == null ? 1 : args.length);
            else if(bytecodes.containsKey(command))
                return 1 + bytecodes.get(command).paramSize;
            else if("utf8".equals(command))
                return utf8Encoded(args[2]).length + Integer.parseInt(args[1]);
            else if("raw".equals(command))
                return args[1].length();
            else if("type".equals(command)) {
                if("object".equals(args[0]) || "array".equals(args[0]))
                    return typeSize;
                return 1;
            }else
                return 0;
        }

        void putBytecode(String name, byte code, String[] params) {
            int paramSize = 0;
            int[] nparams = null;
            if(params != null) {
                nparams = new int[params.length];
                for(int i = 0; i < params.length;++ i) {
                    int k = sizes.get(params[i].toLowerCase());
                    nparams[i] = k;
                    paramSize += k;
                }
            }
            bytecodes.put(name, new Bytecode(code, nparams, paramSize));
        }

        void writei(String command, long val, OutputStream out) throws IOException {
            int size = sizes.get(command);
            writei0(size, val, out);
        }

        void writei0(int size, long val, OutputStream out) throws IOException {
            switch(size) {
                case 1:
                    out.write((byte)val);
                    break;
                case 2:
                {
                    short v = (short)val;
                    out.write((v >> 8) & 0xFF);
                    out.write(v & 0xFF);
                    break;
                }
                case 4:
                {
                    int v = (int)val;
                    out.write((v >> 24) & 0xFF);
                    out.write((v >> 16) & 0xFF);
                    out.write((v >> 8) & 0xFF);
                    out.write(v & 0xFF);
                    break;
                }
                case 8:
                    out.write((int)((val >> 56) & 0xFF));
                    out.write((int)((val >> 48) & 0xFF));
                    out.write((int)((val >> 40) & 0xFF));
                    out.write((int)((val >> 32) & 0xFF));
                    out.write((int)((val >> 24) & 0xFF));
                    out.write((int)((val >> 16) & 0xFF));
                    out.write((int)((val >> 8) & 0xFF));
                    out.write((int)(val & 0xFF));
                    break;
                default:
                {
                    for(int i = 0; i < size; ++i)
                        out.write((byte)(val >>> (size - i - 1) * 8) & 0xFF);
                }
            }
        }

        void writef32(float val, OutputStream out) throws IOException {
            writei0(4, Float.floatToRawIntBits(val), out);
        }

        void writef64(double val, OutputStream out) throws IOException {
            writei0(8, Double.doubleToRawLongBits(val), out);
        }

        void writeType(String type, long[] args, OutputStream out) throws IOException {
            int t = types.get(type);
            out.write(t);
            if(t == types.get("object"))
                writei0(sizes.get("id"), args[0], out);
            else if(t == types.get("array")) {
                writei0(1, args[0], out);
                writei0(1, args[1], out);
            }
        }

        void writeUtf8(boolean ul, int lenSize, String string, OutputStream out) throws IOException {
            byte[] encoded = utf8Encoded(string);
            writei0(lenSize, ul ? string.length() : encoded.length, out);
            out.write(encoded);
        }

        void writeBytecode(String code, long[] args, OutputStream out) throws IOException {
            Bytecode bc = bytecodes.get(code);
            if(bc == null)
                return;
            out.write(bc.code);
            if(bc.params != null) {
                for(int i = 0;i < bc.params.length;++ i)
                    writei0(bc.params[i], args[i], out);
            }
        }
    }

    static class Bytecode {
        final byte code;
        final int[] params;
        final int paramSize;

        Bytecode(byte code, int[] params, int paramSize) {
            this.code = code;
            this.params = params;
            this.paramSize = paramSize;
        }
    }

    static class Command {
        final boolean isAsCmd;
        final String command;
        final String[] args;

        public Command(boolean isAsCmd, String command, String[] args) {
            this.isAsCmd = isAsCmd;
            this.command = command;
            this.args = args;
        }
    }

    static class AssemblyException extends RuntimeException {
        AssemblyException(String msg) {
            super(msg);
        }
    }
}
