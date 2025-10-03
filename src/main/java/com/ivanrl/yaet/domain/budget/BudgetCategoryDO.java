package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;

@DomainModel
public record BudgetCategoryDO(Integer id,
                               Integer categoryId, // TODO Replace with actual object
                               String name, // TODO Replace with actual object
                               BigDecimal amountInherited,
                               BigDecimal amountAssigned,
                               BigDecimal amountSpent) {

    public BudgetCategoryDO(Integer categoryId, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, categoryId, name, amountInherited, amountAssigned, amountSpent);
    }

    // TODO This should be somewhere else
    // It's not the business of the domain to know about the persistence details
    public static BudgetCategoryDO from(SimpleBudgetCategoryDO budgetCategory, BigDecimal amountSpent) {
        return new BudgetCategoryDO(budgetCategory.id(),
                                    budgetCategory.category().id(),
                                    budgetCategory.category().name(),
                                    budgetCategory.amountInherited(),
                                    budgetCategory.amountAssigned(),
                                    amountSpent);
    }

}
