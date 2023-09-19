package splitter.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.entity.Members;
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
            Set<String> membersSet = new HashSet<>();
            process(input, RegexPatterns.PLUS_PATTERN, membersSet::addAll);
            process(input, RegexPatterns.MINUS_PATTERN, membersSet::removeAll);
            switch (command) {
                case "create" -> groupService.createGroup(groupName, membersSet);
                case "add" -> groupService.updateGroup(groupName, membersSet);
                case "remove" -> groupService.removeFromGroup(groupName, membersSet);
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
                .skip(3)
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));


        List<String> groupList = map.getOrDefault(true, Collections.emptyList()); // it's a group name
        List<String> names = map.getOrDefault(false, Collections.emptyList()); // it's a members name

        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(groupService.ungroupNames(groupList));

        action.accept(finalNames);
    }
}

