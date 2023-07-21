package splitter.command;

import splitter.BalanceHistory;
import splitter.BalanceHolder;
import splitter.BalanceType;
import splitter.util.DateUtil;

import java.math.*;
import java.time.LocalDate;
import java.util.*;


public class BalanceProcessor implements CommandProcessor {

    public static final String KEY_DELIMITER = "-";
    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();

    private BalanceType balanceType = BalanceType.close;

    @Override
    public void process(List<String> input) {
        //LocalDate date = DateUtil.getDate(input.get(0));

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
        process(date);
    }

    private void process(LocalDate date) {
        try {
            balanceHolder.getAmountMap().keySet().stream()
                    .map(key -> {
                        String[] name = key.split(KEY_DELIMITER);

                        final LocalDate finalDate = (balanceType == BalanceType.open) ? date.withDayOfMonth(1) : date;
                        BigDecimal totalAmount = balanceHolder.getHistoryMap().get(key).stream()
                                .filter(it -> checkIsBalanceFromHistory(finalDate, it.getDate()))
                                .sorted(Comparator.comparing(BalanceHistory::getDate))
                                .reduce((first, second) -> second)
                                .map(BalanceHistory::getBalance)
                                .orElse(BigDecimal.ZERO);

                        return (totalAmount.signum() == 0) ? "No repayments" : buildRepaymentString(name, totalAmount);
                    })
                    .sorted()
                    .forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Illegal command arguments" + e);
        }
    }

    private static String buildRepaymentString(String[] name, BigDecimal totalAmount) {

        BigDecimal resultTotalAmount = totalAmount.setScale(2, RoundingMode.HALF_EVEN);


        boolean negate = totalAmount.signum() == -1;
        if (negate) {
            resultTotalAmount = resultTotalAmount.negate();
        }

        var first = negate ? name[1] : name[0];
        var second = negate ? name[0] : name[1];

        return String.format("%s owes %s %s", first, second, resultTotalAmount);
    }

    private boolean checkIsBalanceFromHistory(LocalDate date, LocalDate balanceDate) {
        return balanceType == BalanceType.close && !balanceDate.isAfter(date)
                || balanceType == BalanceType.open && balanceDate.isBefore(date);
    }
}
