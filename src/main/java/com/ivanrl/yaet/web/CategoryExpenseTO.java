package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.expense.CategoryExpensesDO;

import java.util.List;

public record CategoryExpenseTO(CategoryTO category,
                                List<BasicExpenseTO> expenses) {

    static CategoryExpenseTO from(CategoryExpensesDO domainObject) {
        return new CategoryExpenseTO(CategoryTO.from(domainObject.category()),
                                     domainObject.expenses()
                                                 .stream()
                                                 .map(BasicExpenseTO::from)
                                                 .toList());
    }
}
