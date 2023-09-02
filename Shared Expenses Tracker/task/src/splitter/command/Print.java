package splitter.command;

import org.springframework.stereotype.Component;
import splitter.BalanceHistory;
import splitter.BalanceHolder;
import splitter.GroupsHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class Print implements CommandProcessor {
    
    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();
    private final GroupsHolder groupsHolder = GroupsHolder.getInstance();

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.print);
    }

    @Override
    public void process(List<String> input) {

        for (Map.Entry<String, BigDecimal> s : balanceHolder.getAmountMap().entrySet()) {
            System.out.println("Amount Map " + s.getKey() + " " + s.getValue());
        }

        for (Map.Entry<String, ArrayList<BalanceHistory>> entry : balanceHolder.getHistoryMap().entrySet()) {
            ArrayList<BalanceHistory> value = entry.getValue();
            System.out.println("History Map " + entry.getKey());
            for (BalanceHistory history : value) {
                System.out.println(history.toString());
            }
        }

        for (Map.Entry<String, List<String>> entry : groupsHolder.getGroupMembers().entrySet()) {
            List<String> value = entry.getValue();
            System.out.println("Group Map" + entry.getKey());
            value.forEach(System.out::println);
        }

        System.out.println("finalList");
        groupsHolder.getFinalListNames().forEach(System.out::println);
    }
}
