package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;

public record NewBudgetCategoryRequest(SimpleCategoryDO category,
                                       BigDecimal amountInherited,
                                       BigDecimal amountAssigned) {
}
