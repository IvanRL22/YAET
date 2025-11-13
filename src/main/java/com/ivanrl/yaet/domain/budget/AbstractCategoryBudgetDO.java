package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import com.ivanrl.yaet.domain.expense.HasAmount;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;


// TODO: Should this object have yearMonth instead of id? Since what identifies it really is yearMonth and category
@DomainModel
@Getter
@Accessors(fluent = true) // TODO Make it a global configuration
public abstract sealed class AbstractCategoryBudgetDO
        permits NormalCategoryBudgetDO, OutOfBudgetCategoryBudgetDO {

    private final Integer id;
    private final SimpleCategoryDO category;
    private final BigDecimal amountAssigned;

    protected AbstractCategoryBudgetDO(Integer id, SimpleCategoryDO category, BigDecimal amountAssigned) {
        this.id = id;
        this.category = category;
        this.amountAssigned = amountAssigned;
    }


    public static AbstractCategoryBudgetDO from(SimpleBudgetCategoryDO simpleBudgetCategoryDO,
                                                List<HasAmount> expenses) {
        return switch (simpleBudgetCategoryDO.category().type()) {
            case OUT_OF_BUDGET -> new OutOfBudgetCategoryBudgetDO(simpleBudgetCategoryDO);
            case NORMAL -> new NormalCategoryBudgetDO(simpleBudgetCategoryDO,
                                                      expenses);
        };
    }

    public static AbstractCategoryBudgetDO fromPreviousMonth(SimpleBudgetCategoryDO simpleBudgetCategoryDO,
                                                             List<HasAmount> orDefault) {
        return switch (simpleBudgetCategoryDO.category().type()) {
            case OUT_OF_BUDGET -> OutOfBudgetCategoryBudgetDO.fromPrevious(simpleBudgetCategoryDO);
            case NORMAL -> NormalCategoryBudgetDO.fromPrevious(simpleBudgetCategoryDO,
                                                               orDefault);
        };
    }

}