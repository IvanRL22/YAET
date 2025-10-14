package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.CategoryDO;

import java.math.BigDecimal;

@DomainModel
public record SimpleBudgetCategoryDO(Integer id,
                                     CategoryDO category,
                                     BigDecimal amountInherited,
                                     BigDecimal amountAssigned) {

    public static SimpleBudgetCategoryDO emptyFrom(CategoryDO c) {
        return new SimpleBudgetCategoryDO(null,
                                          c,
                                          BigDecimal.ZERO,
                                          BigDecimal.ZERO);
    }

    public int categoryId() {
        return category.id();
    }

    public BigDecimal getBalance() {
        return amountInherited.add(amountAssigned);
    }
}
