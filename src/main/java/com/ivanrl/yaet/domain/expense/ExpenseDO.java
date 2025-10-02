package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainModel
public record ExpenseDO(int id, String category, String payee, BigDecimal amount, LocalDate date) {

}
