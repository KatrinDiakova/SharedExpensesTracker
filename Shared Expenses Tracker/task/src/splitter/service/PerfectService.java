package splitter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.ResultsHolder;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.repository.MembersRepository;
import static java.util.Map.Entry.comparingByValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class PerfectService {

    private final MembersRepository membersRepository;

    public PerfectService(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    @Transactional
    public List<String> process(LocalDate date) {
        List<String> results = ResultsHolder.getInstance().getResults();
        List<Balance> balances = convertToBalances(results, date);
        List<Balance> optimizedBalances = optimizeBalances(balances, date);
        return convertToResults(optimizedBalances);
    }

    private List<Balance> optimizeBalances(List<Balance> balances, LocalDate date) {
        Map<Members, BigDecimal> netBalances = calculateNetBalances(balances);

        var debtsBalances = netBalances.entrySet()
                .stream()
                .filter(it -> it.getValue().signum() == -1)
                .sorted(comparingByValue())
                .collect(Collectors.toCollection(ArrayList::new));

        var creditsBalances = netBalances.entrySet()
                .stream()
                .filter(it -> it.getValue().signum() == 1)
                .sorted(comparingByValue())
                .collect(Collectors.toCollection(ArrayList::new));

        List<Balance> optimizedBalances = new ArrayList<>();
        while (!creditsBalances.isEmpty()) {
            optimizedBalances.add(optimizeDebtSettlement(creditsBalances, debtsBalances, date));
        }
        return optimizedBalances;
    }

    private Balance optimizeDebtSettlement(List<Map.Entry<Members, BigDecimal>> creditsBalances,
                                           List<Map.Entry<Members, BigDecimal>> debtsBalances,
                                           LocalDate date) {
        for (var credEntry : creditsBalances) {
            for (var debtEntry : debtsBalances) {
                if (credEntry.getValue().equals(debtEntry.getValue().negate())) {
                    creditsBalances.remove(credEntry);
                    debtsBalances.remove(debtEntry);
                    return new Balance(credEntry.getKey(), debtEntry.getKey(), date, credEntry.getValue());
                }
            }
        }
        var maxCred = creditsBalances.get(0);
        var maxDebt = debtsBalances.get(0);

        if (maxCred.getValue().compareTo(maxDebt.getValue().negate()) > 0) {
            debtsBalances.remove(maxDebt);
            var newValueCred = maxCred.getValue().add(maxDebt.getValue());
            return new Balance(maxCred.getKey(), maxDebt.getKey(), date, maxCred.setValue(newValueCred));
        }
        creditsBalances.remove(maxCred);
        var newValueDeb = maxCred.getValue().add(maxDebt.getValue());
        maxDebt.setValue(newValueDeb);
        return new Balance(maxCred.getKey(), maxDebt.getKey(), date, maxCred.getValue());
    }

    //вычисляем чистые балансы для каждого
    private Map<Members, BigDecimal> calculateNetBalances(List<Balance> balances) {
        Map<Members, BigDecimal> netBalances = new HashMap<>();
        for (Balance balance: balances) {
            netBalances.put(balance.getFromMember(), netBalances.getOrDefault
                    (balance.getFromMember(), BigDecimal.ZERO).add(balance.getAmount()));

            netBalances.put(balance.getToMember(), netBalances.getOrDefault
                    (balance.getToMember(), BigDecimal.ZERO).subtract(balance.getAmount()));
        }
        return netBalances;
    }
    private List<Balance> convertToBalances(List<String> results, LocalDate date) {
        List<Balance> balances = new ArrayList<>();
        for (String result : results) {
            String[] input = result.split(" ");
            String debtorName = input[0];
            String creditorName = input[2];
            BigDecimal amount = new BigDecimal(input[3]);

            var findDebtor = membersRepository.findByMemberName(debtorName);
            Members debtor = findDebtor.orElseGet(() -> membersRepository.save(new Members(debtorName)));

            var findCreditor  = membersRepository.findByMemberName(creditorName);
            Members creditor  = findCreditor.orElseGet(() -> membersRepository.save(new Members(creditorName)));

            balances.add(new Balance(debtor, creditor, date, amount));
        }
        return balances;
    }
    private List<String> convertToResults(List<Balance> optimizedBalances) {
        List<String> results = new ArrayList<>();
        for (Balance balance : optimizedBalances) {
            String debtorName = balance.getFromMember().getMemberName();
            String creditorName = balance.getToMember().getMemberName();
            BigDecimal amount = balance.getAmount();
            results.add(String.format("%s owes %s %.2f", debtorName, creditorName, amount));
        }
        return results;
    }
}
