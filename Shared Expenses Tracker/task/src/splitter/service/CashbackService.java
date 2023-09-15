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

    private final BigDecimal minimumAmount = new BigDecimal("0.01");
    boolean haveRemainder = false;

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
        BigDecimal quantityPerson = new BigDecimal(temporary.size()); // 3 количество учасников транзакции
        AmountCalculator calculator = new AmountCalculator(totalPrice, temporary, payerMember);

        BigDecimal sharedAmount = calculator.calcSharedAmount(quantityPerson);
        boolean haveRemainder = calculator.hasRemainder(sharedAmount, quantityPerson);
        Deque<String> extraPayers = calculator.calcExtraPayers(haveRemainder, sharedAmount, quantityPerson);

        Members company = new Members(payerMember); // to
        membersRepository.save(company);

        temporary.forEach(name -> {
                    //name - from
                    Members person = membersRepository.getByMemberName(name);
                    BigDecimal currentAmount = balanceService.getCurrentAmount(person, company);
                    BigDecimal newAmount = currentAmount.subtract(sharedAmount);
                    if (haveRemainder && name.equals(extraPayers.peek())) {
                        newAmount = newAmount.subtract(minimumAmount);
                        extraPayers.remove();
                    }
                    balanceRepository.save(new Balance(person, company, date, newAmount));
                    transactionsRepository.save(new Transactions("cashback", date, person, company, newAmount));
                });
    }
}