package splitter.command;

import java.util.List;

@FunctionalInterface
public interface CommandProcessor {

    void process(List<String> input);
}
