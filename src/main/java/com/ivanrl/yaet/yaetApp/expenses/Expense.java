package com.ivanrl.yaet.yaetApp.expenses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(int id, String category, String payee, BigDecimal amount, LocalDate date) {

    public static Expense from(ExpensePO e) {
        return new Expense(e.getId(), e.getCategory().getName(), e.getPayee(), e.getAmount(), e.getDate());
    }
}
