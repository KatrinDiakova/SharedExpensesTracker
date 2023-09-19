package splitter;

public enum Command {
    balance,
    borrow,
    cashBack,
    exit,
    group,
    help,
    purchase,
    repay,
    secretSanta,
    writeOff;

    public static Command of(String value) {
        for (Command command : Command.values()) {
            if (value.contains(command.name())) {
                return command;
            }
        }
        return null;
    }
}
