package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;

import java.math.BigDecimal;
import java.util.List;

@DomainModel
// TODO: Should this object have yearMonth instead of id?
// Since what identifies it really is yearMonth and category
public record BudgetCategoryDO(Integer id,
                               SimpleCategoryDO category,
                               BigDecimal amountInherited,
                               BigDecimal amountAssigned,
                               BigDecimal amountSpent) {

    public static BudgetCategoryDO from(SimpleBudgetCategoryDO budgetCategory,
                                        List<ExpenseWithCategoryDO> expenses) {
        var totalSpent = expenses.stream()
                                 .map(ExpenseWithCategoryDO::amount)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BudgetCategoryDO(budgetCategory.id(),
                                    budgetCategory.category(),
                                    budgetCategory.amountInherited(),
                                    budgetCategory.amountAssigned(),
                                    totalSpent);
    }

    // Mostly for convenience, to be used as a method reference
    public String getCategoryName() {
        return category.name();
    }

}
