package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.expense.persistence.ExpensePO;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainModel
public record ExpenseDO(int id, String category, String payee, BigDecimal amount, LocalDate date) {

    public static ExpenseDO from(ExpensePO e) {
        return new ExpenseDO(e.getId(), e.getCategory().getName(), e.getPayee(), e.getAmount(), e.getDate());
    }
}
