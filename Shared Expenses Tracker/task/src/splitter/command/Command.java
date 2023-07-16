package splitter.command;

public enum Command {
    balance,
    borrow,
    exit,
    group,
    help,
    purchase,
    repay;

    public static Command of(String value) {
        for (Command command : Command.values()) {
            if (value.contains(command.name())) {
                return command;
            }
        }
        return null;
        //System.err.println("Unknown command. Print help to show commands list");
        //throw new IllegalArgumentException("Unknown command: " + value);
    }
}
