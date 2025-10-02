package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainModel
public record IncomeDO(int id, String payee, LocalDate date, BigDecimal amount) {

}
