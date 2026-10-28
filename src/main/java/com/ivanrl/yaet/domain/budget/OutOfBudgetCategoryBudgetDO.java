package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import com.ivanrl.yaet.domain.expense.HasAmount;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the monthly budget of a category which must not be aggregated into the budget calculations
 */
@Getter
@Accessors(fluent = true) // TODO Make it a global configuration
@DomainModel
public final class OutOfBudgetCategoryBudgetDO extends AbstractCategoryBudgetDO {

    private final BigDecimal amountInherited;
    private final BigDecimal amountSpent;

    private OutOfBudgetCategoryBudgetDO(Integer id,
                                        SimpleCategoryDO category,
                                        BigDecimal amountAssigned,
                                        BigDecimal amountInherited,
                                        BigDecimal amountSpent) {
        super(id, category, amountAssigned);
        this.amountInherited = amountInherited;
        this.amountSpent = amountSpent;
    }

    public OutOfBudgetCategoryBudgetDO(SimpleBudgetCategoryDO simpleBudgetCategoryDO, List<HasAmount> expenses) {
        super(simpleBudgetCategoryDO.id(),
              simpleBudgetCategoryDO.category(),
              simpleBudgetCategoryDO.amountAssigned());
        this.amountInherited = simpleBudgetCategoryDO.amountInherited();
        this.amountSpent = expenses.stream()
                                   .map(HasAmount::amount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static AbstractCategoryBudgetDO fromPrevious(SimpleBudgetCategoryDO simpleBudgetCategoryDO,
                                                 List<HasAmount> expenses) {
        var totalSpent = expenses.stream()
                                 .map(HasAmount::amount)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = simpleBudgetCategoryDO.amountInherited()
                                                   .add(simpleBudgetCategoryDO.amountAssigned())
                                                   .subtract(totalSpent);
        return new OutOfBudgetCategoryBudgetDO(null,
                                               simpleBudgetCategoryDO.category(),
                                               BigDecimal.ZERO,
                                               balance,
                                               BigDecimal.ZERO);
    }
}
