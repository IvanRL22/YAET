package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;
import java.util.List;

@DomainModel
public record BudgetMonthDO(BigDecimal totalIncome,
                            List<AbstractCategoryBudgetDO> categories) {

}
