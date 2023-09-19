package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.BalanceType;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.repository.BalanceRepository;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

@Service
public class BalanceService {

    private static final String KEY_DELIMITER = "-";

    private final BalanceRepository balanceRepository;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public void process(LocalDate date, BalanceType balanceType) {
        List<Balance> balances = balanceRepository.findAll();
        if (!balances.isEmpty()) {
            Set<String> processedMembers = new HashSet<>();
            List<String> results = new ArrayList<>();

            for (Balance balance : balances) {
                var fromMember = balance.getFromMember();
                var toMember = balance.getToMember();

                String[] name = new String[]{fromMember.getMemberName(), toMember.getMemberName()};
                String memberKey = fromMember.getMemberName() + KEY_DELIMITER + toMember.getMemberName();
                if (processedMembers.contains(memberKey)) {
                    continue;
                }
                //filter all balances records to find all records that match the current member pair (fromMember and toMember).
                List<Balance> memberKeyBalance = balances.stream()
                        .filter(it -> it.getFromMember().equals(fromMember) && it.getToMember().equals(toMember)).toList();
                final LocalDate finalDate = (balanceType == BalanceType.open) ? date.withDayOfMonth(1) : date;

                BigDecimal totalAmount = memberKeyBalance.stream()
                        .filter(it -> checkIsBalanceFromHistory(finalDate, it.getDate(), balanceType))
                        .sorted(Comparator.comparing(Balance::getDate))
                        .reduce((first, second) -> second)
                        .map(Balance::getAmount)
                        .orElse(BigDecimal.ZERO);
                results.add((totalAmount.signum() == 0) ? "No repayments" : buildRepaymentString(name, totalAmount));
                processedMembers.add(memberKey);
            }
            results.stream().sorted().forEach(System.out::println);
        } else {
            System.out.println("No repayments");
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

    private boolean checkIsBalanceFromHistory(LocalDate date, LocalDate balanceDate, BalanceType balanceType) {
        return balanceType == BalanceType.close && !balanceDate.isAfter(date)
                || balanceType == BalanceType.open && balanceDate.isBefore(date);
    }

    public BigDecimal getCurrentAmount(Members mainPerson, Members secondPerson) {
        return balanceRepository.findCurrentAmount(mainPerson, secondPerson)
                .stream()
                .findFirst()
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
