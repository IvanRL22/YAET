package com.ivanrl.yaet.domain.budget;

import java.math.BigDecimal;
import java.util.List;

public record BudgetMonthDO(BigDecimal totalIncome,
                            List<BudgetCategoryDO> categories) {

}
