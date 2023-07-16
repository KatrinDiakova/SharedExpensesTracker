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

public class PurchaseProcessor implements CommandProcessor {

    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();
    private final GroupsHolder groupsHolder = GroupsHolder.getInstance();
    private final Set<String> temporary = new TreeSet<>();

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

            process(input, RegexPatterns.PLUS_PATTERN, temporary::addAll);
            process(input, RegexPatterns.MINUS_PATTERN, temporary::removeAll);

            haveRemainder = false;
            processPurchase(date, payerPerson, totalPrice);

        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }

    private void process(List<String> input, Pattern PATTERN, Consumer<List<String>> action) {
        Map<Boolean, List<String>> map = input.stream()
                .skip(3)
                .flatMap(it -> PATTERN.matcher(it).results())
                .map(MatchResult::group)
                .collect(Collectors.partitioningBy(it -> RegexPatterns.GROUP_PATTERN.matcher(it).matches()));

        List<String> namesFromGroup = map.getOrDefault(true, Collections.emptyList())
                .stream()
                .flatMap(it -> groupsHolder.getGroupMembers().getOrDefault(it, Collections.emptySet()).stream())
                .toList();

        List<String> names = map.getOrDefault(false, Collections.emptyList());

        if (namesFromGroup.isEmpty()) {
            System.out.println("Group is empty");
        } else {
            List<String> finalNames = new ArrayList<>();
            finalNames.addAll(names);
            finalNames.addAll(namesFromGroup);
            action.accept(finalNames);
        }
    }

    private void processPurchase(LocalDate date, String payerPerson, BigDecimal totalPrice) {
        BigDecimal quantityPerson = new BigDecimal(temporary.size());
        BigDecimal sharedAmount = totalPrice.divide(quantityPerson, RoundingMode.FLOOR);
        haveRemainder = !totalPrice.equals(sharedAmount.multiply(quantityPerson));
        BigDecimal remainder = haveRemainder ? totalPrice.subtract(sharedAmount.multiply(quantityPerson)) : BigDecimal.ZERO;
        int extraPayers = haveRemainder ? remainder.divide(minimumAmount, RoundingMode.DOWN).intValue() : 0;
        int count = 0;
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
                    BigDecimal newBalance = BigDecimal.ZERO;
                    if (haveRemainder && count < extraPayers) {
                        newBalance = calculateNewBalance(nameKey, newBalance, currentAmount, sharedAmount, true);
                    } else {
                        newBalance = calculateNewBalance(nameKey, newBalance, currentAmount, sharedAmount, false);
                    }
                    balanceHolder.setAmount(nameKey.getSortedKey(), newBalance);
                    balanceHistoriesList.add(new BalanceHistory(date, newBalance));
                    balanceHolder.setHistory(nameKey.getSortedKey(), balanceHistoriesList);
                });
    }

    private BigDecimal calculateNewBalance(NameKey nameKey, BigDecimal newBalance, BigDecimal currentAmount, BigDecimal sharedAmount, boolean isHaveRemainder) {
        if (nameKey.getSortedKey().equals(nameKey.getKey())) {
            return isHaveRemainder ? newBalance.subtract(minimumAmount) : currentAmount.subtract(sharedAmount);
        } else {
            return isHaveRemainder ? newBalance.add(minimumAmount) : currentAmount.add(sharedAmount);
        }
    }
}

