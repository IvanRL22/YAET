package com.ivanrl.yaet.web;

import com.ivanrl.yaet.UsedInTemplate;
import com.ivanrl.yaet.domain.budget.AbstractCategoryBudgetDO;
import com.ivanrl.yaet.domain.budget.NormalCategoryBudgetDO;
import com.ivanrl.yaet.domain.budget.OutOfBudgetCategoryBudgetDO;
import com.ivanrl.yaet.domain.category.CategoryType;

import java.math.BigDecimal;

public record BudgetCategoryTO(Integer id,
                        Integer categoryId,
                        String name,
                        CategoryType type,
                        int order,
                        BigDecimal amountInherited,
                        BigDecimal amountAssigned,
                        BigDecimal amountSpent) implements Comparable<BudgetCategoryTO> {

    private BudgetCategoryTO(OutOfBudgetCategoryBudgetDO outOfBudget) {
        this(outOfBudget.id(),
             outOfBudget.category().id(),
             outOfBudget.category().name(),
             CategoryType.OUT_OF_BUDGET,
             outOfBudget.category().order(),
             BigDecimal.ZERO,
             outOfBudget.amountAssigned(),
             BigDecimal.ZERO);
    }

    private BudgetCategoryTO(NormalCategoryBudgetDO normal) {
        this(normal.id(),
             normal.category().id(),
             normal.category().name(),
             CategoryType.NORMAL,
             normal.category().order(),
             normal.amountInherited(),
             normal.amountAssigned(),
             normal.amountSpent());
    }

    static BudgetCategoryTO from(AbstractCategoryBudgetDO domainObject) {
        return switch (domainObject) {
            case OutOfBudgetCategoryBudgetDO outOfBudget -> new BudgetCategoryTO(outOfBudget);
            case NormalCategoryBudgetDO normal -> new BudgetCategoryTO(normal);
        };
    }

    @Override
    public int compareTo(BudgetCategoryTO o) {
        return this.order - o.order;
    }

    public BigDecimal getTotalAmount() {
        if (CategoryType.OUT_OF_BUDGET.equals(type)) {
            return BigDecimal.ZERO;
        }

        return amountAssigned.add(amountInherited)
                             .subtract(amountSpent);

    }

    @UsedInTemplate
    public boolean isOutOfBudget() {
        return CategoryType.OUT_OF_BUDGET.equals(type);
    }
}
