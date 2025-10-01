package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.domain.income.persistence.IncomePO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeDO(int id, String payee, LocalDate date, BigDecimal amount) {

    public static IncomeDO from(IncomePO po) {
        return new IncomeDO(po.getId(), po.getPayer(), po.getDate(), po.getAmount());
    }
}
