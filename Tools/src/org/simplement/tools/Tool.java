package org.simplement.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Tool implements Runnable {
    protected final String[] args;
    protected final String usage;

    public Tool(String[] args, int minArgAmount, String usage) {
        this.args = args;
        this.usage = usage;
        if(args.length < minArgAmount) {
            System.out.println("Not enough arguments. Usage: " + usage);
            System.exit(0);
        }
    }

    protected static List<String> readMarkerLines(String markerStart, String markerEnd, Scanner scanner) {
        List<String> lines = new ArrayList<>();
        boolean flag = false;
        String line;
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.contains(markerStart)) {
                flag = true;
                continue;
            }else if(line.contains(markerEnd))
                return lines;
            if(flag)
                lines.add(line);
        }
        return lines;
    }
}
