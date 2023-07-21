package splitter.command;

import splitter.*;
import splitter.util.DateUtil;
import splitter.util.RegexPatterns;

import java.math.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseProcessor implements CommandProcessor {

    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();
    private final GroupsHolder groupsHolder = GroupsHolder.getInstance();

    BigDecimal minimumAmount = new BigDecimal("0.01");
    boolean haveRemainder = false;

    @Override
    public void process(List<String> input) {
        try {
            LocalDate date = LocalDate.now();
            int commandIndex = 0;

            if (DateUtil.isDate(input.get(0))) {
                date = DateUtil.getDate(input.get(0));
                commandIndex = 1;
            }
            String payerPerson = input.get(commandIndex + 1);
            BigDecimal totalPrice = new BigDecimal(input.get(commandIndex + 3));

            Set<String> temporary = new TreeSet<>();
            process(input,commandIndex, RegexPatterns.PLUS_PATTERN, temporary::addAll);
            process(input, commandIndex, RegexPatterns.MINUS_PATTERN, temporary::removeAll);

            haveRemainder = false;
            processPurchase(date, payerPerson, totalPrice, temporary);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
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

        List<String> gropNames = map.getOrDefault(true, Collections.emptyList());
        List<String> namesFromGroup = gropNames.stream()
                .flatMap(it -> groupsHolder.getGroupMembers().getOrDefault(it, Collections.emptyList()).stream())
                .toList();
        if (namesFromGroup.isEmpty() && !gropNames.isEmpty()) {
            throw new IllegalArgumentException("Group is empty");
        }
        List<String> names = map.getOrDefault(false, Collections.emptyList());
        List<String> finalNames = new ArrayList<>();
        finalNames.addAll(names);
        finalNames.addAll(namesFromGroup);
        action.accept(finalNames);
    }

    private void processPurchase(LocalDate date, String payerPerson, BigDecimal totalPrice, Set<String> temporary) {
        BigDecimal quantityPerson = new BigDecimal(temporary.size());
        BigDecimal sharedAmount = totalPrice.divide(quantityPerson, RoundingMode.FLOOR);

        haveRemainder = !totalPrice.equals(sharedAmount.multiply(quantityPerson));
        BigDecimal remainder = haveRemainder ? totalPrice.subtract(sharedAmount.multiply(quantityPerson)) : BigDecimal.ZERO;
        int extraPayersCount = haveRemainder ? remainder.divide(minimumAmount, RoundingMode.DOWN).intValue() : 0;

        List<String> temporaryList = new ArrayList<>(temporary);
        temporaryList.remove(payerPerson);

        Deque<String> extraPayers = IntStream.range(0, extraPayersCount)
                .mapToObj(temporaryList::get)
                .collect(Collectors.toCollection(LinkedList::new));

        temporary.stream()
                .filter(name -> !name.equals(payerPerson))
                .forEach(name -> {
                    NameKey nameKey = new NameKey(payerPerson, name);
                    ArrayList<BalanceHistory> balanceHistoriesList = balanceHolder
                            .getHistoryMap()
                            .computeIfAbsent(nameKey.getSortedKey(), list -> new ArrayList<>());
                    BigDecimal currentAmount = balanceHolder
                            .getAmountMap()
                            .computeIfAbsent(nameKey.getSortedKey(), d -> BigDecimal.ZERO);
                    BigDecimal newBalance;
                    newBalance = nameKey.getSortedKey().equals(nameKey.getKey()) ? currentAmount.subtract(sharedAmount) : currentAmount.add(sharedAmount);

                    if (haveRemainder && name.equals(extraPayers.peek())) {
                        newBalance = nameKey.getSortedKey().equals(nameKey.getKey()) ? newBalance.subtract(minimumAmount) : newBalance.add(minimumAmount);
                        extraPayers.remove();
                    }
                    balanceHolder.setAmount(nameKey.getSortedKey(), newBalance);
                    balanceHistoriesList.add(new BalanceHistory(date, newBalance));
                    balanceHolder.setHistory(nameKey.getSortedKey(), balanceHistoriesList);
                });
    }
}

