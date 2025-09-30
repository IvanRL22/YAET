package com.ivanrl.yaet.yaetApp.domain;

import com.ivanrl.yaet.yaetApp.domain.category.CategoryDO;
import com.ivanrl.yaet.yaetApp.domain.expense.ExpenseDO;

import java.util.List;

// What would be the correct domain for this object?
public record CategoryExpensesDO(CategoryDO category, List<ExpenseDO> expenses) {
}
