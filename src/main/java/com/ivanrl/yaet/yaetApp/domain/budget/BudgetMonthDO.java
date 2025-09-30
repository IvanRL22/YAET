package com.ivanrl.yaet.yaetApp.domain.budget;

import java.math.BigDecimal;
import java.util.List;

public record BudgetMonthDO(BigDecimal totalIncome,
                            List<BudgetCategoryDO> categories) {

}
