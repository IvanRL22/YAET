package com.ivanrl.yaet.web;

import com.ivanrl.yaet.UsedInTemplate;
import com.ivanrl.yaet.domain.budget.BudgetCategoryDO;

import java.math.BigDecimal;

public record BudgetCategoryTO(Integer id,
                        Integer categoryId,
                        String name,
                        BigDecimal amountInherited,
                        BigDecimal amountAssigned,
                        BigDecimal amountSpent) {

    static BudgetCategoryTO from(BudgetCategoryDO domainObject) {
        return new BudgetCategoryTO(domainObject.id(),
                                    domainObject.category().id(),
                                    domainObject.category().name(),
                                    domainObject.amountInherited(),
                                    domainObject.amountAssigned(),
                                    domainObject.amountSpent());
    }

    @UsedInTemplate
    public BigDecimal getTotalAmount() {
        return amountAssigned.add(amountInherited).subtract(amountSpent);
    }
}
