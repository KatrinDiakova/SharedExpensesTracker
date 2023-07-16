package splitter.command;

import splitter.BalanceHistory;
import splitter.BalanceHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Print implements CommandProcessor {
    
    private final BalanceHolder balanceHolder = BalanceHolder.getInstance();
    
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
    }
}
