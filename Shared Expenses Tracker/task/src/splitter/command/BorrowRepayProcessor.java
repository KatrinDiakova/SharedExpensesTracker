package splitter.command;

import splitter.BalanceHistory;
import splitter.BalanceHolder;
import splitter.NameKey;
import splitter.util.DateUtil;

import java.math.*;
import java.time.LocalDate;
import java.util.*;



public class BorrowRepayProcessor implements CommandProcessor {

    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();

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

            if (Command.borrow == command || Command.repay == command) {
                NameKey nameKey = new NameKey(extractPersonOne(input, commandIndex), extractPersonTwo(input, commandIndex));
                BigDecimal amount = new BigDecimal(extractAmount(input, commandIndex)).setScale(2, RoundingMode.HALF_EVEN);
                process(date, command, amount, nameKey);
            }
        } catch (Exception e) {
            System.out.println("Illegal command arguments");

        }
    }

    private String extractPersonOne(List<String> input, int commandIndex) {
        return input.get(commandIndex + 1);
    }

    private String extractPersonTwo(List<String> input, int commandIndex) {
        return input.get(commandIndex + 2);
    }

    private String extractAmount(List<String> input, int commandIndex) {
        return input.get(commandIndex + 3);
    }

    private void process(LocalDate date, Command command, BigDecimal amount, NameKey nameKey) {

        ArrayList<BalanceHistory> balanceHistoriesList = balanceHolder
                .getHistoryMap()
                .computeIfAbsent(nameKey.getSortedKey(), list -> new ArrayList<>());

        BigDecimal currentAmount = balanceHolder
                .getAmountMap()
                .computeIfAbsent(nameKey.getSortedKey(), d -> BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal newBalance = BigDecimal.ZERO;
        boolean keyEquals = nameKey.getSortedKey().equals(nameKey.getKey());
        switch (command) {
            case borrow -> newBalance = keyEquals ? currentAmount.add(amount) : currentAmount.subtract(amount);
            case repay -> newBalance = keyEquals ? currentAmount.subtract(amount) : currentAmount.add(amount);
        }

        balanceHolder.setAmount(nameKey.getSortedKey(), newBalance.setScale(2, RoundingMode.HALF_EVEN));
        balanceHistoriesList.add(new BalanceHistory(date, newBalance.setScale(2, RoundingMode.HALF_EVEN)));
        balanceHolder.setHistory(nameKey.getSortedKey(), balanceHistoriesList);
    }
}
