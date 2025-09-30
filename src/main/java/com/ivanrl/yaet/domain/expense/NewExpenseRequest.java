package com.ivanrl.yaet.domain.expense;

import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NewExpenseRequest(Integer categoryId, String payee, BigDecimal amount, LocalDate date, String comment) {

    public static NewExpenseRequest empty() {
        return new NewExpenseRequest(null, Strings.EMPTY, null, LocalDate.now(), Strings.EMPTY);
    }
}
