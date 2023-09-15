//package splitter.service;
//
//import org.springframework.stereotype.Service;
//import splitter.Command;
//import splitter.CommandProcessor;
//import splitter.repository.BalanceRepository;
//import splitter.repository.GroupsRepository;
//import splitter.repository.MembersRepository;
//import splitter.repository.TransactionsRepository;
//import java.util.List;
//
//@Service
//public class ClearService implements CommandProcessor {
//
//    private final TransactionsRepository transactionsRepository;
//    private final BalanceRepository balanceRepository;
//    private final MembersRepository membersRepository;
//    private final GroupsRepository groupsRepository;
//
//    public ClearService(TransactionsRepository transactionsRepository, BalanceRepository balanceRepository, MembersRepository membersRepository, GroupsRepository groupsRepository) {
//        this.transactionsRepository = transactionsRepository;
//        this.balanceRepository = balanceRepository;
//        this.membersRepository = membersRepository;
//        this.groupsRepository = groupsRepository;
//    }
//
//    @Override
//    public void process(List<String> input) {
//        balanceRepository.deleteAll();
//        transactionsRepository.deleteAll();
//        groupsRepository.deleteAll();
//        membersRepository.deleteAll();
//
//    }
//
//    @Override
//    public List<Command> getCommand() {
//        return List.of(Command.clear);
//    }
//}
