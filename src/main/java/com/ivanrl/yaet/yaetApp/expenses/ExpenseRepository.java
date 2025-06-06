package com.ivanrl.yaet.yaetApp.expenses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpensePO, Integer> {

    // Ordering is not strictly needed, I'm just getting it DB side
    @Query("""
            FROM expenses e
            JOIN FETCH e.category c
            WHERE :from <= e.date
            AND e.date <= :to
            ORDER BY e.date ASC
            """)
    List<ExpensePO> findAllByDateBetween(@Param("from") LocalDate from,
                                         @Param("to") LocalDate to);

    @Query("""
            FROM expenses e
            JOIN FETCH e.category c
            ORDER BY e.date DESC
            """)
    Page<ExpensePO> findLastExpenses(Pageable pagination);
}
