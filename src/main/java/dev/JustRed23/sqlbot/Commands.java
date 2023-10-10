package dev.JustRed23.sqlbot;

public enum Commands {

    OPEN_SQLCMD("sqlcmd -S .\\SQLEXPRESS");

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
