package com.ivanrl.yaet.yaetApp.domain.budget;

import com.ivanrl.yaet.yaetApp.UsedInTemplate;

import java.math.BigDecimal;

public record BudgetCategoryDO(Integer id, Integer categoryId, String name, BigDecimal amountInherited,
                               BigDecimal amountAssigned, BigDecimal amountSpent) {

    public BudgetCategoryDO(Integer categoryId, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, categoryId, name, amountInherited, amountAssigned, amountSpent);
    }

    public static BudgetCategoryDO from(BudgetCategoryProjection projection, BigDecimal amountSpent) {
        return new BudgetCategoryDO(projection.getId(),
                                    projection.getCategoryId(),
                                    projection.getName(),
                                    projection.getAmountInherited(),
                                    projection.getAmountAssigned(),
                                    amountSpent);
    }

    @UsedInTemplate
    public BigDecimal getTotalAmount() {
        return amountAssigned.add(amountInherited).subtract(amountSpent);
    }
}
