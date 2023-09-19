package splitter.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.entity.Members;
import splitter.service.CashbackService;
import splitter.service.GroupService;
import splitter.service.PurchaseService;
import splitter.util.*;
import java.math.*;
import java.time.LocalDate;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.*;

@Component
public class PurchaseCashbackParser implements CommandProcessor {

    private final GroupService groupService;
    private final PurchaseService purchaseService;
    private final CashbackService cashbackService;

    @Autowired
    public PurchaseCashbackParser(GroupService groupService, PurchaseService purchaseService, CashbackService cashbackService) {
        this.groupService = groupService;
        this.purchaseService = purchaseService;
        this.cashbackService = cashbackService;
    }

    @Override
    public List<Command> getCommand() {
        return List.of(Command.purchase, Command.cashBack);
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
            Command command = Command.of(input.get(commandIndex));
            String payerMember = input.get(commandIndex + 1);
            BigDecimal totalPrice = new BigDecimal(input.get(commandIndex + 3));

            Set<String> temporary = new TreeSet<>();
            process(input, commandIndex, RegexPatterns.PLUS_PATTERN, temporary::addAll);
            process(input, commandIndex, RegexPatterns.MINUS_PATTERN, temporary::removeAll);

            switch (command) {
                case purchase -> purchaseService.process(date, payerMember, totalPrice, temporary);
                case cashBack -> cashbackService.process(date, payerMember, totalPrice, temporary);
            }
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }

    private void process(List<String> input, int index, Pattern PATTERN, Consumer<List<String>> action) {
        Map<Boolean, List<String>> map = input.stream()
                .skip(index + 4)
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

