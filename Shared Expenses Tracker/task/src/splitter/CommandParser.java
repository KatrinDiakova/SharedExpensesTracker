package splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

@Component
public class CommandParser {

    private static final String INPUT_DELIMITER = " ";

    private final Map<Command, CommandProcessor> processors = new HashMap<>();

    @Autowired
    public CommandParser(List<CommandProcessor> processorList) {
        for (CommandProcessor processor : processorList) {
            for (Command command : processor.getCommand()) {
                processors.put(command, processor);
            }
        }
        processors.put(Command.help, (List<String> inputs) ->
                Stream.of(Command.values())
                        .sorted(Comparator.comparing(Enum::name))
                        .forEach(System.out::println));
    }

    boolean parseUserInput(String input) {
        var command = Command.of(input);
        if (command == Command.exit) {
            return true;
        } else if (command == null) {
            System.out.println("Unknown command. Print help to show commands list");
            return false;
        }
        var inputList = List.of(input.split(INPUT_DELIMITER));
        processors.getOrDefault(command, System.err::println).process(inputList);
        return false;
    }
}