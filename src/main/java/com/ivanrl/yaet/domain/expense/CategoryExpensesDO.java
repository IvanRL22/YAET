package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.CategoryDO;

import java.util.List;

@DomainModel
public record CategoryExpensesDO(CategoryDO category, List<ExpenseDO> expenses) {
}
