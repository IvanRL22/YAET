package com.ivanrl.yaet.yaetApp.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IncomeRepository extends JpaRepository<IncomePO, Integer> {

    @Query("""
            select coalesce(sum(amount), 0)
            from incomes
            where date >= :from
            and date <= :to
            """)
    BigDecimal getTotalIncome(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
