package splitter.command;

import org.springframework.stereotype.Component;
import splitter.GroupsHolder;
import splitter.util.RegexPatterns;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.*;

@Component
public class GroupProcessor implements CommandProcessor {

    private final GroupsHolder groupsHolder = GroupsHolder.getInstance();
    private final Map<String, List<String>> groupMembers = groupsHolder.getGroupMembers();

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.group);
    }

    @Override
    public void process(List<String> input) {
        runSafely(() -> {
            String command = input.get(1);
            String groupName = RegexPatterns.GROUP_PATTERN.matcher(input.get(2))
                    .results()
                    .reduce((first, second) -> second)
                    .map(MatchResult::group)
                    .orElseThrow(() -> new IllegalArgumentException("Illegal command arguments"));

            Set<String> newFinalListNames = new HashSet<>();
            groupsHolder.setFinalListNames(newFinalListNames);
            process(input, RegexPatterns.PLUS_PATTERN, newFinalListNames::addAll);
            process(input, RegexPatterns.MINUS_PATTERN, newFinalListNames::removeAll);

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
                .skip(3) // (+Bob, GIRLS, -Frank, Chuck)
                // элементы потока проверяются на соответсвие патерну ( сначала PLUS_PATTERN, затем MINUS_PATTERN)
                // для PLUS_PATTERN поток Bob, GIRLS, Chuck, для MINUS_PATTERN Frank
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));

        // true GIRLS
        List<String> namesFromGroup = map.getOrDefault(true, Collections.emptyList())
                .stream()
                .flatMap(it -> groupMembers.getOrDefault(it, Collections.emptyList()).stream()) //заменить на базу данных
                .toList();

        // false Bob, Chuck
        List<String> names = map.getOrDefault(false, Collections.emptyList());

        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(namesFromGroup);

        action.accept(finalNames);
    }

    private void createGroup(String groupName) {
        groupMembers.putIfAbsent(groupName, groupsHolder.getFinalListNames().stream().sorted().toList()); // сохраняем в базу данных имя группы и имена участников
    }

    private void changeGroup(String groupName) {
        // проверяем существует ли введенная группа в базе
        if (groupMembers.containsKey(groupName)) {
            // обновляет или добавляет значение в groupMembers для ключа groupName, объединяя текущий список имен с другим списком из groupsHolder.
            groupMembers.compute(groupName, (key, names) -> Stream.concat(names.stream(), groupsHolder.getFinalListNames().stream())
                    .sorted()
                    .collect(Collectors.toList()));
        } else {
            System.out.println("Group doesn't exist");
        }
    }

    private void removeFromGroup(String groupName) {
        // проверяем существует ли введенная группа в базе
        if (groupMembers.containsKey(groupName)) {
            groupMembers.compute(groupName, (key, names) -> names.stream()
                    //сохраняете только те имена, которые отсутствуют в списке FinalListNames().
                    .filter(name -> !groupsHolder.getFinalListNames().contains(name))
                    .sorted()
                    .collect(Collectors.toList()));
        } else {
            System.out.println("Group doesn't exist");
        }
    }

    private void showGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) {
            System.out.println("Unknown group");
        } else {
            List<String> names = groupMembers.get(groupName);
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

