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
            String command = input.get(commandIndex);

            if ("borrow".equals(command) || "repay".equals(command)) {
                NameKey nameKey = new NameKey(input.get(commandIndex + 1), input.get(commandIndex + 2));
                BigDecimal amount = new BigDecimal((input.get(commandIndex + 3))).setScale(2, RoundingMode.HALF_EVEN);
                process(date, command, amount, nameKey);
            }
        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }

    private void process(LocalDate date, String command, BigDecimal amount, NameKey nameKey) {

        ArrayList<BalanceHistory> balanceHistoriesList = balanceHolder
                .getHistoryMap()
                .computeIfAbsent(nameKey.getSortedKey(), list -> new ArrayList<>());

        BigDecimal currentAmount = balanceHolder
                .getAmountMap()
                .computeIfAbsent(nameKey.getSortedKey(), d -> BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal newBalance = BigDecimal.ZERO;
        if (command.equals("borrow")) {
            newBalance = calculateNewBalance(nameKey, currentAmount, amount, true);
        } else if (command.equals("repay")) {
            newBalance = calculateNewBalance(nameKey, currentAmount, amount, false);
        }

        balanceHolder.setAmount(nameKey.getSortedKey(), newBalance.setScale(2, RoundingMode.HALF_EVEN));
        balanceHistoriesList.add(new BalanceHistory(date, newBalance.setScale(2, RoundingMode.HALF_EVEN)));
        balanceHolder.setHistory(nameKey.getSortedKey(), balanceHistoriesList);
    }

    private BigDecimal calculateNewBalance(NameKey nameKey, BigDecimal currentAmount, BigDecimal amount, boolean isBorrow) {
        if (nameKey.getSortedKey().equals(nameKey.getKey())) {
            return isBorrow ? currentAmount.add(amount) : currentAmount.subtract(amount);
        } else {
            return isBorrow ? currentAmount.subtract(amount) : currentAmount.add(amount);
        }
    }
}
