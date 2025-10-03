package com.ivanrl.yaet.web;

import com.ivanrl.yaet.UsedInTemplate;
import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.persistence.category.CategoryPO;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

// TODO Replace with CategoryDO
record Category(Integer id, String name, String description) {

    public static Category from(CategoryPO po) {
        return new Category(po.getId(), po.getName(), po.getDescription());
    }
}

// TODO Replace with CategoryExpensesDO
record CategoryExpense(String category, BigDecimal totalAmount, List<ExpenseDO> expenses) {}

// TODO Extract as domain object - Probably exctract the total quantities as its own domain object aswell
record MonthOverview(YearMonth month,
                     List<CategoryExpense> categories,
                     BigDecimal totalExpense,
                     BigDecimal totalIncome,
                     BigDecimal balance) {

    public static MonthOverview from(YearMonth month, List<CategoryExpense> categories, BigDecimal totalIncome) {
        var totalExpense = categories.stream()
                .map(CategoryExpense::totalAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return new MonthOverview(month, categories, totalExpense, totalIncome, totalIncome.subtract(totalExpense));
    }

    @UsedInTemplate
    public String getMonthText() {
        return "%s of %d".formatted(month.getMonth(), month.getYear());
    }
}
