package com.ivanrl.yaet.yaetApp.domain.expense;

import com.ivanrl.yaet.yaetApp.domain.CategoryExpensesDO;
import com.ivanrl.yaet.yaetApp.domain.category.CategoryDO;
import com.ivanrl.yaet.yaetApp.expenses.CategoryPO;
import com.ivanrl.yaet.yaetApp.expenses.CategoryRepository;
import com.ivanrl.yaet.yaetApp.expenses.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class SeeExpensesUseCase {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public CategoryExpensesDO getExpenses(int categoryId,
                                          YearMonth month) {
        var from = month.atDay(1);
        var to = month.atEndOfMonth();
        CategoryPO categoryPO = categoryRepository.findById(categoryId).orElseThrow(); // TODO - Create specific exception
        var expenses = expenseRepository.findAllByCategoryAndDateBetween(categoryId, from, to)
                                        .stream()
                                        .map(ExpenseDO::from)
                                        .toList();

        return new CategoryExpensesDO(CategoryDO.from(categoryPO),
                                      expenses);
    }
}
