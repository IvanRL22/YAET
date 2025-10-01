package com.ivanrl.yaet.domain.income;

import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NewIncomeRequest(String payer, BigDecimal amount, LocalDate date) {

    public static NewIncomeRequest empty() {
        return new NewIncomeRequest(Strings.EMPTY, null, LocalDate.now());
    }
}
