package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.expense.ExpenseDO;

import java.math.BigDecimal;
import java.time.LocalDate;

record BasicExpenseTO(int id, String category, String payee, BigDecimal amount, LocalDate date) {

    public static BasicExpenseTO from (ExpenseDO domainObject) {
        return new BasicExpenseTO(domainObject.id(),
                                  domainObject.category(),
                                  domainObject.payee(),
                                  domainObject.amount(),
                                  domainObject.date());
    }
}
