package ru.liga.packagefactory.domain.enums;

public enum ConsoleCommand {
    EXIT("exit"),
    IMPORT("import"),
    LOAD("load"),
    UNLOAD("unload"),
    UNKNOWN("unknown");

    private final String command;

    ConsoleCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static ConsoleCommand fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }

        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        for (ConsoleCommand cmd : ConsoleCommand.values()) {
            if (cmd.command.equals(command)) {
                return cmd;
            }
        }
        return UNKNOWN;
    }

    public static boolean isExitCommand(String input) {
        return EXIT.command.equals(input.toLowerCase().trim());
    }

    public static boolean isImportCommand(String input) {
        return input.toLowerCase().startsWith(IMPORT.command + " ");
    }

    public static boolean isLoadCommand(String input) {
        return input.toLowerCase().startsWith(LOAD.command + " ");
    }

    public static boolean isUnloadCommand(String input) {
        return input.toLowerCase().startsWith(UNLOAD.command + " ");
    }
}
