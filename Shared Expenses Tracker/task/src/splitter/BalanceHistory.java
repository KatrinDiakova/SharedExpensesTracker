package splitter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BalanceHistory {
    private LocalDate date;
    private BigDecimal balance;

    public BalanceHistory(LocalDate date, BigDecimal balance) {
        this.date = date;
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BalanceHistory {" +
                "date=" + date +
                ", balance=" + balance +
                '}';
    }
}
