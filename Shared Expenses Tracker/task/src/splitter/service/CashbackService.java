package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.AmountCalculator;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.entity.Transactions;
import splitter.repository.BalanceRepository;
import splitter.repository.MembersRepository;
import splitter.repository.TransactionsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CashbackService {

    private final BalanceRepository balanceRepository;
    private final MembersRepository membersRepository;
    private final TransactionsRepository transactionsRepository;
    private final BalanceService balanceService;

    @Autowired
    public CashbackService(BalanceRepository balanceRepository, MembersRepository membersRepository, BorrowRepayService borrowRepayService, TransactionsRepository transactionsRepository, BalanceService balanceService) {
        this.balanceRepository = balanceRepository;
        this.membersRepository = membersRepository;
        this.transactionsRepository = transactionsRepository;
        this.balanceService = balanceService;
    }

    @Transactional
    public void process(LocalDate date, String payerMember, BigDecimal totalPrice, Set<String> temporary) {
        BigDecimal quantityPerson = new BigDecimal(temporary.size());
        AmountCalculator calculator = new AmountCalculator(totalPrice, temporary, payerMember);

        BigDecimal sharedAmount = calculator.calcSharedAmount(quantityPerson);
        boolean haveRemainder = calculator.hasRemainder(sharedAmount, quantityPerson);
        Deque<String> extraPayers = calculator.calcExtraPayers(haveRemainder, sharedAmount, quantityPerson);

        Optional<Members> findCompany = membersRepository.findByMemberName(payerMember);
        Members company = findCompany.orElseGet(() -> membersRepository.save(new Members(payerMember))); // to

        temporary.forEach(name -> {
            //name - from
            Optional<Members> findPerson = membersRepository.findByMemberName(name);
            Members person = findPerson.orElseGet(() -> membersRepository.save(new Members(name)));

            BigDecimal currentAmount = balanceService.getCurrentAmount(person, company);
            BigDecimal newAmount = currentAmount.subtract(sharedAmount);
            if (haveRemainder && name.equals(extraPayers.peek())) {
                newAmount = newAmount.subtract(calculator.getMinimumAmount());
                extraPayers.remove();
            }
            balanceRepository.save(new Balance(person, company, date, newAmount));
            transactionsRepository.save(new Transactions("cashback", date, person, company, newAmount));
        });
    }
}