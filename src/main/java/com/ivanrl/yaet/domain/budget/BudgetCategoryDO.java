package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;

@DomainModel
// TODO: Should this object have yearMonth instead of id?
// Since what identifies it really is yearMonth and category
public record BudgetCategoryDO(Integer id,
                               SimpleCategoryDO category,
                               BigDecimal amountInherited,
                               BigDecimal amountAssigned,
                               BigDecimal amountSpent) {

    public BudgetCategoryDO(SimpleCategoryDO category,
                            BigDecimal amountInherited,
                            BigDecimal amountAssigned,
                            BigDecimal amountSpent) {
        this(null, category, amountInherited, amountAssigned, amountSpent);
    }

    public static BudgetCategoryDO from(SimpleBudgetCategoryDO budgetCategory,
                                        BigDecimal amountSpent) {
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
