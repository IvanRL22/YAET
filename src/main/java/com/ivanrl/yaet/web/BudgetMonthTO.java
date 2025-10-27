package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.budget.BudgetMonthDO;

import java.math.BigDecimal;
import java.util.List;

record BudgetMonthTO(BigDecimal totalIncome,
                     List<BudgetCategoryTO> categories) {

    static BudgetMonthTO from(BudgetMonthDO domainObject) {
        return new BudgetMonthTO(domainObject.totalIncome(),
                                 domainObject.categories()
                                             .stream()
                                             .map(BudgetCategoryTO::from)
                                             .sorted()
                                             .toList());
    }
}
