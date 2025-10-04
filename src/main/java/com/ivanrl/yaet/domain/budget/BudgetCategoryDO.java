package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.CategoryDO;

import java.math.BigDecimal;

@DomainModel
public record BudgetCategoryDO(Integer id,
                               CategoryDO category,
                               BigDecimal amountInherited,
                               BigDecimal amountAssigned,
                               BigDecimal amountSpent) {

    public BudgetCategoryDO(CategoryDO category, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, category, amountInherited, amountAssigned, amountSpent);
    }

    public static BudgetCategoryDO from(SimpleBudgetCategoryDO budgetCategory, BigDecimal amountSpent) {
        return new BudgetCategoryDO(budgetCategory.id(),
                                    budgetCategory.category(),
                                    budgetCategory.amountInherited(),
                                    budgetCategory.amountAssigned(),
                                    amountSpent);
    }

    // Mostly for convenience, to be used as a method reference
    public String getCategoryName() {
        return category.name();
    }

}
