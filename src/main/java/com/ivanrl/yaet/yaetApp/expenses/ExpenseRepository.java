package com.ivanrl.yaet.yaetApp.expenses;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpensePO, Integer> {

    Sort defaultSorting = Sort.by(Sort.Direction.ASC, "date");

    List<ExpensePO> findAllByDateBetween(LocalDate from, LocalDate to, Sort defaultSorting);
}
