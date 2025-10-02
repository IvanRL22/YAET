package com.ivanrl.yaet.domain;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.expense.ExpenseDO;

import java.util.List;

@DomainModel
// What would be the correct domain for this object?
public record CategoryExpensesDO(CategoryDO category, List<ExpenseDO> expenses) {
}
