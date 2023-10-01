package splitter.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "from_member_id")
    private Members fromMember;

    @ManyToOne
    @JoinColumn(name = "to_member_id")
    private Members toMember;

    private LocalDate date;

    private BigDecimal amount;

    public Balance(Members fromMember, Members toMember, LocalDate date, BigDecimal amount) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.date = date;
        this.amount = amount;
    }

    public Balance() {}

    public Members getFromMember() {
        return fromMember;
    }

    public Members getToMember() {
        return toMember;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Balance {" +
                "balanceId= " + balanceId +
                ", date= " + date +
                ", fromMember= " + fromMember.getMemberName() +
                ", toMember= " + toMember.getMemberName() +
                ", amount= " + amount +
                '}';
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
