package com.ivanrl.yaet.yaetApp.domain.budget;

import com.ivanrl.yaet.yaetApp.domain.budget.persistence.BudgetCategoryPO;
import com.ivanrl.yaet.yaetApp.domain.budget.persistence.BudgetCategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryPO;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CopyFromPreviousUseCase {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public void copyFor(YearMonth month) {

        var previous = month.minusMonths(1);
        var allCategories = this.categoryRepository.findAll();
        var currentMonthBudgets = this.budgetCategoryRepository.findAll(month);

        List<CategoryPO> categoriesToGenerate;
        if (currentMonthBudgets.isEmpty()) {
            categoriesToGenerate = allCategories;
        } else {
            var categoriesWithBudget = currentMonthBudgets.stream()
                                                          .map(BudgetCategoryProjection::getCategoryId)
                                                          .collect(Collectors.toSet());
            categoriesToGenerate = allCategories.stream()
                                                .filter(c -> !categoriesWithBudget.contains(c.getId()))
                                                .toList();
        }

        Set<BudgetCategoryProjection> budgetCategoryProjections = this.budgetCategoryRepository.findAll(previous,
                                                                                                        categoriesToGenerate.stream().map(CategoryPO::getId).collect(Collectors.toSet()));

        var expenses = this.expenseRepository.findAllWithCategory(previous.atDay(1),
                                                                  previous.atEndOfMonth(),
                                                                  budgetCategoryProjections.stream()
                                                                                           .map(BudgetCategoryProjection::getCategoryId)
                                                                                           .collect(Collectors.toSet()));

        var expensesByCategory = expenses.stream().collect(Collectors.groupingBy(e -> e.getCategory().getId()));

        var result = budgetCategoryProjections.stream()
                                              .map(bc -> createBudgetCategory(month, bc, expensesByCategory.getOrDefault(bc.getCategoryId(),
                                                                                                                         Collections.emptyList())))
                                              .toList();

        this.budgetCategoryRepository.saveAll(result);
    }

    private BudgetCategoryPO createBudgetCategory(YearMonth month,
                                                  BudgetCategoryProjection bc,
                                                  List<ExpensePO> expensePOS) {
        var totalSpent = expensePOS.stream()
                                   .map(ExpensePO::getAmount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BudgetCategoryPO(this.categoryRepository.getReferenceById(bc.getCategoryId()),
                                    month,
                                    bc.getAmountInherited()
                                      .add(bc.getAmountAssigned())
                                      .subtract(totalSpent),
                                    bc.getAmountAssigned());
    }
}
