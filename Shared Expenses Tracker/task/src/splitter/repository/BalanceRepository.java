package splitter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import splitter.entity.Balance;
import splitter.entity.Members;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    void deleteByDateLessThanEqual(LocalDate date);

    @Query("SELECT b.amount FROM Balance b WHERE b.fromMember = :personOne AND b.toMember = :personTwo ORDER BY b.date DESC, b.balanceId DESC")
    List<BigDecimal> findCurrentAmount(@Param("personOne") Members personOne, @Param("personTwo") Members personTwo);
}
