package splitter.command;

import splitter.GroupsHolder;
import splitter.util.RegexPatterns;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.*;


public class GroupProcessor implements CommandProcessor {

    private final GroupsHolder groupsHolder = GroupsHolder.getInstance();
    private final Set<String> finalListNames = groupsHolder.getFinalListNames();
    private final Map<String, Set<String>> groupMembers = groupsHolder.getGroupMembers();

    @Override
    public void process(List<String> input) {
        runSafely(() -> {
            String command = input.get(1);
            String groupName = RegexPatterns.GROUP_PATTERN.matcher(input.get(2))
                    .results()
                    .reduce((first, second) -> second)
                    .map(MatchResult::group)
                    .orElseThrow(() -> new IllegalArgumentException("Illegal command arguments"));


            process(input, RegexPatterns.PLUS_PATTERN, finalListNames::addAll);
            process(input, RegexPatterns.MINUS_PATTERN, finalListNames::removeAll);

            switch (command) {
                case "create" -> createGroup(groupName);
                case "add" -> changeGroup(groupName);
                case "remove" -> removeFromGroup(groupName);
                case "show" -> showGroup(groupName);
                default -> throw new IllegalArgumentException();
            }
        });
    }

    private void process(List<String> input, Pattern PATTERN, Consumer<List<String>> action) {
        Map<Boolean, List<String>> map = input.stream()
                .skip(3)
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));

        List<String> namesFromGroup = map.getOrDefault(true, Collections.emptyList())
                .stream()
                .flatMap(it -> groupMembers.getOrDefault(it, Collections.emptySet()).stream())
                .toList();

        List<String> names = map.getOrDefault(false, Collections.emptyList());

        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(namesFromGroup);

        action.accept(finalNames);
    }

    private void createGroup(String groupName) {
        groupMembers.putIfAbsent(groupName, new TreeSet<>());
        groupMembers.get(groupName).addAll(finalListNames);
    }

    private void changeGroup(String groupName) {
        groupMembers.computeIfPresent(groupName, (key, names) -> {
            names.addAll(finalListNames);
            return names;
        });
        if (!groupMembers.containsKey(groupName)) {
            System.out.println("Group doesn't exist");
        }
    }

    private void removeFromGroup(String groupName) {
        groupMembers.computeIfPresent(groupName, (key, names) -> {
            names.removeAll(groupsHolder.getFinalListNames());
            return names;
        });
        if (!groupMembers.containsKey(groupName)) {
            System.out.println("Group doesn't exist");
        }
    }

    private void showGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) {
            System.out.println("Unknown group");
        } else {
            Set<String> names = groupMembers.get(groupName);
            if (names.isEmpty()) {
                System.out.println("Group is empty");
            } else {
                names.forEach(System.out::println);
            }
        }
    }

    private void runSafely(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }
}

