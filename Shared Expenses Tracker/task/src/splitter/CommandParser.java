package splitter;

import splitter.command.*;

import java.util.*;
import java.util.stream.Stream;

public class CommandParser {

    private static final String INPUT_DELIMITER = " ";

    private final Map<Command, CommandProcessor> processors = Map.of(
            Command.group, new GroupProcessor(),
            Command.help, (List<String> inputs) -> Stream.of(Command.values()).forEach(System.out::println),
            Command.borrow, new BorrowRepayProcessor(),
            Command.repay, new BorrowRepayProcessor(),
            Command.balance, new BalanceProcessor(),
            Command.purchase, new PurchaseProcessor()
            //Command.print, new Print()
    );

    boolean parseUserInput(String input) {
        var command = Command.of(input);
        if (command == Command.exit) {
            return true;
        } else if (command == null) {
            System.out.println("Unknown command. Print help to show commands list");
            return false;
        }
        // 2я проверка на наличие команды
        var inputList = List.of(input.split(INPUT_DELIMITER));
        processors.getOrDefault(command, System.err::println).process(inputList);
        return false;
    }
}
