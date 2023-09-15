package splitter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitter.entity.Transactions;

import java.time.LocalDate;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    void deleteByDateLessThanEqual(LocalDate date);
}
