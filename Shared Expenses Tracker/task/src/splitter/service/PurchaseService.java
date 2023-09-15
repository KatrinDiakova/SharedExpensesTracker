package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.util.NameKey;
import splitter.AmountCalculator;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.entity.Transactions;
import splitter.repository.BalanceRepository;
import splitter.repository.MembersRepository;
import splitter.repository.TransactionsRepository;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

@Service
public class PurchaseService {

    BigDecimal minimumAmount = new BigDecimal("0.01");

    private final BalanceRepository balanceRepository;
    private final MembersRepository membersRepository;
    private final TransactionsRepository transactionsRepository;
    private final BalanceService balanceService;

    @Autowired
    public PurchaseService(BalanceRepository balance, MembersRepository members, TransactionsRepository transactions, BalanceService balanceService) {
        this.balanceRepository = balance;
        this.membersRepository = members;
        this.transactionsRepository = transactions;
        this.balanceService = balanceService;
    }

    @Transactional
    public void process(LocalDate date, String payerPerson, BigDecimal totalPrice, Set<String> temporary) {
        BigDecimal quantityPerson = new BigDecimal(temporary.size()); // 3 количество учасников транзакции
        AmountCalculator calculator = new AmountCalculator(totalPrice, temporary, payerPerson);

        BigDecimal sharedAmount = calculator.calcSharedAmount(quantityPerson);
        boolean haveRemainder = calculator.hasRemainder(sharedAmount, quantityPerson);
        Deque<String> extraPayers = calculator.calcExtraPayers(haveRemainder, sharedAmount, quantityPerson);

        Optional<Members> findPayerPerson = membersRepository.findByMemberName(payerPerson);
        Members personOne = findPayerPerson.orElseGet(() -> membersRepository.save(new Members(payerPerson)));

        temporary.stream()
                .filter(name -> !name.equals(payerPerson))
                .forEach(name -> {
                    Optional<Members> findPersonTwo = membersRepository.findByMemberName(name);
                    Members personTwo = findPersonTwo.orElseGet(() -> membersRepository.save(new Members(name)));

                    boolean keyEquals = NameKey.isKeyEquals(payerPerson, name);
                    var mainPerson = keyEquals ? personOne : personTwo;
                    var secondPerson = keyEquals ? personTwo : personOne;

                    BigDecimal currentAmount = balanceService.getCurrentAmount(mainPerson, secondPerson);

                    BigDecimal newAmount;
                    newAmount = keyEquals ? currentAmount.subtract(sharedAmount) : currentAmount.add(sharedAmount);

                    if (haveRemainder && name.equals(extraPayers.peek())) {
                        newAmount = keyEquals ? newAmount.subtract(minimumAmount) : newAmount.add(minimumAmount);
                        extraPayers.remove();
                    }
                    balanceRepository.save(new Balance(mainPerson, secondPerson, date, newAmount));
                    transactionsRepository.save(new Transactions("purchase", date, personTwo, personOne, newAmount));
                });

    }
}






