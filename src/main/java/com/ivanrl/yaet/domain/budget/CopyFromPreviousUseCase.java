package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;
import com.ivanrl.yaet.persistence.budget.BudgetCategoryDAO;
import com.ivanrl.yaet.persistence.category.CategoryDAO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CopyFromPreviousUseCase {

    private final CategoryDAO categoryDAO;
    private final BudgetCategoryDAO budgetCategoryDAO;
    private final ExpenseDAO expenseDAO;

    public void copyFor(YearMonth month) {

        var previousMonth = month.minusMonths(1);
        var allCategories = this.categoryDAO.getAll();
        var currentMonthBudgets = this.budgetCategoryDAO.findAllBy(month);

        List<CategoryDO> categoriesToGenerate;
        if (currentMonthBudgets.isEmpty()) {
            categoriesToGenerate = allCategories;
        } else {
            var categoriesWithBudget = currentMonthBudgets.stream()
                                                          .map(SimpleBudgetCategoryDO::categoryId)
                                                          .collect(Collectors.toSet());
            categoriesToGenerate = allCategories.stream()
                                                .filter(c -> !categoriesWithBudget.contains(c.id()))
                                                .toList();
        }

        List<SimpleBudgetCategoryDO> budgetCategoryProjections = this.budgetCategoryDAO.findAllBy(previousMonth,
                                                                                                  categoriesToGenerate.stream()
                                                                                                                      .map(CategoryDO::id)
                                                                                                                      .collect(Collectors.toSet()));

        var expenses = this.expenseDAO.findAllBy(previousMonth,
                                                 budgetCategoryProjections.stream()
                                                                          .map(SimpleBudgetCategoryDO::categoryId)
                                                                          .collect(Collectors.toSet()));

        var expensesByCategory = expenses.stream().collect(Collectors.groupingBy(ExpenseWithCategoryDO::getCategoryId));

        var result = budgetCategoryProjections.stream()
                                              .map(bc -> createBudgetCategory(bc, expensesByCategory.getOrDefault(bc.categoryId(),
                                                                                                                         Collections.emptyList())))
                                              .toList();

        this.budgetCategoryDAO.saveAll(result, month);
    }

    private BudgetCategoryDO createBudgetCategory(SimpleBudgetCategoryDO budgetCategory,
                                                  List<ExpenseWithCategoryDO> expenses) {
        var totalSpent = expenses.stream()
                                   .map(ExpenseWithCategoryDO::amount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BudgetCategoryDO.from(budgetCategory,
                                     totalSpent);
    }
}
