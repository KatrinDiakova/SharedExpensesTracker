package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.BalanceType;
import splitter.Command;
import splitter.ResultsHolder;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.repository.BalanceRepository;

import java.math.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BalanceService {

    private static final String KEY_DELIMITER = "-";

    private final BalanceRepository balanceRepository;
    private final PerfectService balancePerfect;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository, PerfectService balancePerfect) {
        this.balanceRepository = balanceRepository;
        this.balancePerfect = balancePerfect;
    }

    @Transactional
    public void process(LocalDate date, BalanceType balanceType, Set<String> temporary, Command command) {
        List<Balance> balances = balanceRepository.findAll();
        ResultsHolder.getInstance().clearResults();

        if (!balances.isEmpty()) {
            Set<String> processedMembers = new HashSet<>();
            List<String> results = ResultsHolder.getInstance().getResults();

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
            if (command == Command.balancePerfect) {
                results = balancePerfect.process(date);
            }
            filterPrintResults(results, temporary);
        } else {
            System.out.println("No repayments");
        }
    }

    private void filterPrintResults(List<String> results, Set<String> temporary) {
        if (!temporary.isEmpty()) {
            results = results.stream()
                    .filter(it -> {
                        String ownMember = it.split(" ")[0];
                        return temporary.contains(ownMember);
                    })
                    .collect(Collectors.toList());
            if (results.isEmpty()) {
                System.out.println("No repayments");
            }
        }
        results.stream().sorted().forEach(System.out::println);
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

    public boolean checkIsBalanceFromHistory(LocalDate date, LocalDate balanceDate, BalanceType balanceType) {
        return balanceType == BalanceType.close && !balanceDate.isAfter(date)
                || balanceType == BalanceType.open && balanceDate.isBefore(date);
    }

    public BigDecimal getCurrentAmount(Members mainPerson, Members secondPerson) {
        return balanceRepository.findCurrentAmount(mainPerson, secondPerson)
                .stream()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
