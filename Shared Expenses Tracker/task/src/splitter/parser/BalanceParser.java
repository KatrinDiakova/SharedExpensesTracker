package splitter.parser;

import org.springframework.stereotype.Component;
import splitter.BalanceType;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.service.BalanceService;
import splitter.service.GroupService;
import splitter.util.DateUtil;
import splitter.util.RegexPatterns;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class BalanceParser implements CommandProcessor {

    private BalanceType balanceType = BalanceType.close;
    private final BalanceService balanceService;
    private final GroupService groupService;

    public BalanceParser(BalanceService balanceService, GroupService groupService) {
        this.balanceService = balanceService;
        this.groupService = groupService;
    }

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.balance);
    }

    @Override
    public void process(List<String> input) {
        try {
            LocalDate date = LocalDate.now();
            int commandIndex = 0;

            if (DateUtil.isDate(input.get(0))) {
                date = DateUtil.getDate(input.get(0));
                commandIndex = 1;
            }

            if (input.size() > commandIndex + 1) {
                Optional.ofNullable(input.get(commandIndex + 1))
                        .filter(it -> Arrays.stream(BalanceType.values())
                                .anyMatch(balanceType -> balanceType.name().equals(it)))
                        .map(BalanceType::valueOf)
                        .ifPresent(it -> balanceType = it);
            }
            Set<String> temporary = new TreeSet<>();
            process(input, commandIndex, RegexPatterns.PLUS_PATTERN, temporary::addAll);
            process(input, commandIndex, RegexPatterns.MINUS_PATTERN, temporary::removeAll);

            balanceService.process(date, balanceType, temporary);
        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }
    private void process(List<String> input, int index, Pattern PATTERN, Consumer<List<String>> action) {
        Map<Boolean, List<String>> map = input.stream()
                .skip(index + 2)
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));

        List<String> gropList = map.getOrDefault(true, Collections.emptyList());
        Set<String> namesFromGroup = groupService.ungroupNames(gropList);

        List<String> names = map.getOrDefault(false, Collections.emptyList());
        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(namesFromGroup);
        action.accept(finalNames);
    }
}
