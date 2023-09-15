package splitter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.util.NameKey;
import splitter.Command;
import splitter.entity.Balance;
import splitter.entity.Members;
import splitter.entity.Transactions;
import splitter.repository.*;

import java.math.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowRepayService {

    private final MembersRepository membersRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionsRepository transactionsRepository;
    private final BalanceService balanceService;

    public BorrowRepayService(MembersRepository membersRepository, BalanceRepository balanceRepository, TransactionsRepository transactionsRepository, BalanceService balanceService) {
        this.membersRepository = membersRepository;
        this.balanceRepository = balanceRepository;
        this.transactionsRepository = transactionsRepository;
        this.balanceService = balanceService;
    }

    @Transactional
    public void process(LocalDate date, Command command, BigDecimal amount, String person1, String person2) {
        Optional<Members> memberFirst = membersRepository.findByMemberName(person1);
        Optional<Members> memberSecond = membersRepository.findByMemberName(person2);

        Members personOne = memberFirst.orElseGet(() -> membersRepository.save(new Members(person1)));
        Members personTwo = memberSecond.orElseGet(() -> membersRepository.save(new Members(person2)));

        boolean keyEquals = NameKey.isKeyEquals(person1, person2);
        var mainPerson = keyEquals ? personOne : personTwo;
        var secondPerson = keyEquals ? personTwo : personOne;

        BigDecimal currentAmount = balanceService.getCurrentAmount(mainPerson, secondPerson);
        BigDecimal newAmount = calculateNewAmount(command, amount, currentAmount, keyEquals);

        balanceRepository.save(new Balance(mainPerson, secondPerson, date, newAmount));
        transactionsRepository.save(new Transactions(command.name(), date, personOne, personTwo, amount));
    }

    private BigDecimal calculateNewAmount(Command command, BigDecimal amount, BigDecimal currentAmount, boolean keyEquals) {
        return switch (command) {
            case borrow -> keyEquals ? currentAmount.add(amount) : currentAmount.subtract(amount);
            case repay -> keyEquals ? currentAmount.subtract(amount) : currentAmount.add(amount);
            default -> null;
        };
    }
}
