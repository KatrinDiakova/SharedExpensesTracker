package splitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceHolder {

    private static final BalanceHolder instance = new BalanceHolder();

    private final Map<String, BigDecimal> amountMap = new HashMap<>();
    private final Map<String, ArrayList<BalanceHistory>> historyMap = new HashMap<>();

    private BalanceHolder() {
    }

    public static BalanceHolder getInstance() {
        return instance;
    }

    public Map<String, BigDecimal> getAmountMap() {
        return amountMap;
    }

    public Map<String, ArrayList<BalanceHistory>> getHistoryMap() {
        return historyMap;
    }

    public void setAmount(String key, BigDecimal number) {
        amountMap.put(key, number);
    }

    public void setHistory(String key, ArrayList<BalanceHistory> list) {
        historyMap.put(key, list);
    }

}
