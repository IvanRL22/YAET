package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;

/**
 * Represents the monthly budget of a category which must not be aggregated into the budget calculations
 */
@DomainModel
public final class OutOfBudgetCategoryBudgetDO extends AbstractCategoryBudgetDO {

    public OutOfBudgetCategoryBudgetDO(SimpleBudgetCategoryDO simpleBudgetCategoryDO) {
        super(simpleBudgetCategoryDO.id(),
              simpleBudgetCategoryDO.category(),
              simpleBudgetCategoryDO.amountAssigned());
    }

    private OutOfBudgetCategoryBudgetDO(Integer id, SimpleCategoryDO category, BigDecimal amountAssigned) {
        super(id, category, amountAssigned);
    }

    static AbstractCategoryBudgetDO fromPrevious(SimpleBudgetCategoryDO simpleBudgetCategoryDO) {
        return new OutOfBudgetCategoryBudgetDO(null,
                                               simpleBudgetCategoryDO.category(),
                                               BigDecimal.ZERO);
    }
}
