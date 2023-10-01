package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.repository.BalanceRepository;
//import splitter.repository.TransactionsRepository;
import splitter.util.DateUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class WriteOffService implements CommandProcessor {

    private final BalanceRepository balanceRepository;
    //private final TransactionsRepository transactionsRepository;

    @Autowired
    public WriteOffService(BalanceRepository balanceRepository) {
                           //TransactionsRepository transactionsRepository) {
        this.balanceRepository = balanceRepository;
        //this.transactionsRepository = transactionsRepository;
    }

    @Override
    @Transactional
    public void process(List<String> input) {
        LocalDate date = DateUtil.isDate(input.get(0)) ? DateUtil.getDate(input.get(0)) : LocalDate.now();
        balanceRepository.deleteByDateLessThanEqual(date);
        //transactionsRepository.deleteByDateLessThanEqual(date);

    }

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.writeOff);
    }
}
