package com.ivanrl.yaet.domain.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

// TODO Define and assign proper annotation, DomainObject perhaps
public record UpdateExpenseRequest(int id,
                                   int categoryId,
                                   String payee,
                                   LocalDate date,
                                   BigDecimal amount,
                                   String comment) {
}
