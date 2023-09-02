package splitter.command;

import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface CommandProcessor {

    void process(List<String> input);

    default List<Command> getCommand() {
        return Collections.emptyList();
    }
}
