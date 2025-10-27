package com.ivanrl.yaet.web;

import com.ivanrl.yaet.UsedInTemplate;
import com.ivanrl.yaet.domain.budget.BudgetCategoryDO;

import java.math.BigDecimal;

public record BudgetCategoryTO(Integer id,
                        Integer categoryId,
                        String name,
                        int order,
                        BigDecimal amountInherited,
                        BigDecimal amountAssigned,
                        BigDecimal amountSpent) implements Comparable<BudgetCategoryTO> {

    static BudgetCategoryTO from(BudgetCategoryDO domainObject) {
        return new BudgetCategoryTO(domainObject.id(),
                                    domainObject.category().id(),
                                    domainObject.category().name(),
                                    domainObject.category().order(),
                                    domainObject.amountInherited(),
                                    domainObject.amountAssigned(),
                                    domainObject.amountSpent());
    }

    @Override
    public int compareTo(BudgetCategoryTO o) {
        return this.order - o.order;
    }

    @UsedInTemplate
    public BigDecimal getTotalAmount() {
        return amountAssigned.add(amountInherited).subtract(amountSpent);
    }
}
