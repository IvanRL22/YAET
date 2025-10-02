package com.ivanrl.yaet.domain.budget;

import java.math.BigDecimal;

public record BudgetCategoryDO(Integer id,
                               Integer categoryId, // TODO Replace with actual object
                               String name, // TODO Replace with actual object
                               BigDecimal amountInherited,
                               BigDecimal amountAssigned,
                               BigDecimal amountSpent) {

    public BudgetCategoryDO(Integer categoryId, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, categoryId, name, amountInherited, amountAssigned, amountSpent);
    }

    // TODO This should be somewhere else
    // It's not the business of the domain to know about the persistence details
    public static BudgetCategoryDO from(BudgetCategoryProjection projection, BigDecimal amountSpent) {
        return new BudgetCategoryDO(projection.getId(),
                                    projection.getCategoryId(),
                                    projection.getName(),
                                    projection.getAmountInherited(),
                                    projection.getAmountAssigned(),
                                    amountSpent);
    }

}
