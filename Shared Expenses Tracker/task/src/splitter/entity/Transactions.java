package splitter.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    private String type;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "from_member_id")
    private Members fromMember;

    @ManyToOne
    @JoinColumn(name = "to_member_id")
    private Members toMember;

    private BigDecimal amount;

    public Transactions(String type, LocalDate date, Members fromMember, Members toMember, BigDecimal amount) {
        this.type = type;
        this.date = date;
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.amount = amount;
    }

    public Transactions() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public Members getFromMember() {
        return fromMember;
    }

    public Members getToMember() {
        return toMember;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
