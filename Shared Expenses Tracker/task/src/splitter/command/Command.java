package splitter.command;

public enum Command {
    balance,
    borrow,
    repay,
    exit,
    help,
    group,
    purchase,
    secretSanta,
    cashback,
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
