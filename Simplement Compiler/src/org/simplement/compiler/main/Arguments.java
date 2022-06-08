package org.simplement.compiler.main;

import org.simplement.compiler.generic.Pair;

import java.util.*;
import java.util.stream.Collectors;

public final class Arguments {
    private final String commandName;
    private final List<Pair<String, String>> requiredArgs;
    private final Map<String, Option> options;
    private final Map<String, Setting> settings;

    private Arguments(String commandName, List<Pair<String, String>> requiredArgs, Map<String, Option> options, Map<String, Setting> settings) {
        this.commandName = commandName;
        this.requiredArgs = requiredArgs;
        this.options = options;
        this.settings = settings;
    }

    public boolean parse(String[] args) {
        if(args.length == 0 || args[0].contains("help") || args[0].contains("?")) {
            System.out.println("Usage: " + commandName + " " + String.join(" ", requiredArgs.stream()
                    .map(arg -> "<" + arg.getFirst() + ">").collect(Collectors.toList())) + " [-options,...] [--settings,...]");
            if(!options.isEmpty()) {
                System.out.println("Options:");
                options.forEach((name, option) -> System.out.println("    -" + name + ": " + option.description));
            }
            if(!settings.isEmpty()) {
                System.out.println("Settings:");
                settings.forEach((name, setting) -> System.out.println("    --" + name + "=?: " + setting.description));
            }
            return false;
        }

        int requiredArgAmt = 0;
        for(String arg : args) {
            if(arg.startsWith("--")) {
                if(!arg.contains("=")) {
                    System.out.println("Invalid argument, \'=\' required: " + arg);
                    return false;
                }
                int index = arg.indexOf('=');
                String settingName = arg.substring(2, index);
                Setting setting = settings.get(settingName.toLowerCase());
                if(setting == null)
                    System.out.println("Invalid setting name: " + settingName);
                else
                    setting.value = arg.substring(index + 1);
            }else if(arg.startsWith("-")) {
                String optionName = arg.substring(1);
                Option option = options.get(optionName.toLowerCase());
                if(option == null)
                    System.out.println("Invalid setting name: " + optionName);
                else
                    option.value = true;
            }else{
                if(requiredArgAmt >= requiredArgs.size())
                    System.out.println("Ignoring extra argument: " + arg);
                else
                    requiredArgs.get(requiredArgAmt++).setSecond(arg);
            }
        }
        if(requiredArgAmt < requiredArgs.size()) {
            System.out.println("Not enough arguments specified.");
            return false;
        }
        return true;
    }

    public String getArgument(String name) {
        Pair<String, String> argument = requiredArgs.stream().filter(arg -> arg.getFirst().equals(name)).findAny().orElse(null);;
        return argument == null ? null : argument.getSecond();
    }

    public boolean getOption(String name) {
        return options.get(name).value;
    }

    public String getSetting(String name) {
        return settings.get(name).value;
    }

    static final class Option {
        final String description;
        boolean value;

        Option(String description) {
            this.description = description;
            this.value = false;
        }
    }

    static final class Setting {
        final String description;
        String value;

        Setting(String description, String defaultValue) {
            this.description = description;
            this.value = defaultValue;
        }
    }

    public static final class Builder {
        private final String commandName;
        private List<Pair<String, String>> requiredArgs;
        private Map<String, Option> options;
        private Map<String, Setting> settings;

        private void init() {
            this.requiredArgs = new ArrayList<>();
            this.options = new HashMap<>();
            this.settings = new HashMap<>();
        }

        public Builder(String commandName) {
            this.commandName = commandName;
            init();
        }

        public Builder addRequiredArgument(String name) {
            Objects.requireNonNull(name);
            if(requiredArgs.stream().anyMatch(arg -> arg.getFirst().equalsIgnoreCase(name)))
                throw new IllegalArgumentException("Duplicate argument name: " + name);
            requiredArgs.add(new Pair<>(name.toLowerCase(), null));
            return this;
        }

        public Builder addOption(String name, String description) {
            Objects.requireNonNull(name);
            name = name.toLowerCase();
            if(options.containsKey(name))
                throw new IllegalArgumentException("Duplicate option name: " + name);
            options.put(name, new Option(description));
            return this;
        }

        public Builder addSetting(String name, String description, String defaultValue) {
            Objects.requireNonNull(name);
            name = name.toLowerCase();
            if(settings.containsKey(name))
                throw new IllegalArgumentException("Duplicate setting name: " + name);
            settings.put(name, new Setting(description, defaultValue));
            return this;
        }

        public Arguments build() {
            Arguments args = new Arguments(commandName, requiredArgs, options, settings);
            init();
            return args;
        }
    }
}
