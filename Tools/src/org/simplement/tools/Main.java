package org.simplement.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Main {
    private static final Map<String, Function<String[], Tool>> TOOLS = new HashMap<>();

    static {
        TOOLS.put("bct", BytecodeTool::new);
        TOOLS.put("as", AssemblerTool::new);
    }

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: stls <tool> [args...]");
            return;
        }

        Function<String[], Tool> toolBuilder = TOOLS.get(args[0]);
        if(toolBuilder == null) {
            System.out.println("Invalid tool name: " + args[0]);
            return;
        }

        int len = args.length - 1;
        String[] splicedArgs = new String[len];
        System.arraycopy(args, 1, splicedArgs, 0, len);
        toolBuilder.apply(splicedArgs).run();
    }
}
