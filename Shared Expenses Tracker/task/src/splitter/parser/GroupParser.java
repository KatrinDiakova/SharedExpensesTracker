package splitter.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.service.GroupService;
import splitter.util.RegexPatterns;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.*;

@Component
public class GroupParser implements CommandProcessor {

    private final GroupService groupService;

    @Autowired
    public GroupParser(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.group);
    }

    @Override
    public void process(List<String> input) {
        try {
            String command = input.get(1);
            String groupName = RegexPatterns.GROUP_PATTERN.matcher(input.get(2))
                    .results()
                    .reduce((first, second) -> second)
                    .map(MatchResult::group)
                    .orElseThrow(() -> new IllegalArgumentException("Illegal command arguments"));
            List<String> membersList = new ArrayList<>();
            process(input, RegexPatterns.PLUS_PATTERN, membersList::addAll);
            process(input, RegexPatterns.MINUS_PATTERN, membersList::removeAll);
            switch (command) {
                case "create" -> groupService.createGroup(groupName, membersList);
                case "add" -> groupService.updateGroup(groupName, membersList);
                case "remove" -> groupService.removeFromGroup(groupName, membersList);
                case "show" -> groupService.showGroup(groupName);
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Illegal command arguments");
        }
    }

    private void process(List<String> input, Pattern PATTERN, Consumer<List<String>> action) {
        Map<Boolean, List<String>> map = input.stream()
                .skip(3) // (+Bob, GIRLS, -Frank, Chuck)
                // элементы потока проверяются на соответсвие патерну ( сначала PLUS_PATTERN, затем MINUS_PATTERN)
                // для PLUS_PATTERN поток Bob, GIRLS, Chuck, для MINUS_PATTERN Frank
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));

        // true GIRLS
        List<String> groupList = map.getOrDefault(true, Collections.emptyList());

        // false Bob, Chuck
        List<String> names = map.getOrDefault(false, Collections.emptyList());

        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(groupService.ungroupNames(groupList));

        action.accept(finalNames);
    }
}

