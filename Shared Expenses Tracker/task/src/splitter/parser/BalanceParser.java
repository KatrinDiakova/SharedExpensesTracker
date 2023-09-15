package splitter.parser;

import org.springframework.stereotype.Component;
import splitter.BalanceType;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.service.BalanceService;
import splitter.util.DateUtil;

import java.time.LocalDate;
import java.util.*;

@Component
public class BalanceParser implements CommandProcessor {

    private BalanceType balanceType = BalanceType.close;
    private final BalanceService balanceService;

    public BalanceParser(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.balance);
    }

    @Override
    public void process(List<String> input) {
        try {
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
            balanceService.process(date, balanceType);
        } catch (Exception e) {
            System.out.println("Illegal command arguments");
        }
    }
}
