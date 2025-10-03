package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.category.CategoryDO;

import java.math.BigDecimal;

public record SimpleBudgetCategoryDO(Integer id,
                                     CategoryDO category,
                                     BigDecimal amountInherited,
                                     BigDecimal amountAssigned) {

    public int categoryId() {
        return category.id();
    }

    public BigDecimal getBalance() {
        return amountInherited.add(amountAssigned);
    }
}
