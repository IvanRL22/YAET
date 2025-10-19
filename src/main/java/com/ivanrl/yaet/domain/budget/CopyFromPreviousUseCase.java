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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        var categoriesWithDefaultAmount = allCategories.stream()
                                                       .filter(c -> c.defaultAmount() != null)
                                                       .collect(Collectors.toSet());

        List<CategoryDO> categoriesWithoutCurrentBudget = new ArrayList<>(allCategories);
        var categoriesWithBudgetIds = currentMonthBudgets.stream()
                                                         .map(SimpleBudgetCategoryDO::categoryId)
                                                         .collect(Collectors.toSet());

        categoriesWithoutCurrentBudget.removeIf(c -> categoriesWithBudgetIds.contains(c.id()));

        List<SimpleBudgetCategoryDO> budgetCategoryProjections = this.budgetCategoryDAO.findAllBy(previousMonth,
                                                                                                  categoriesWithoutCurrentBudget.stream()
                                                                                                                      .map(CategoryDO::id)
                                                                                                                      .collect(Collectors.toSet()));

        var expenses = this.expenseDAO.findAllBy(previousMonth,
                                                 budgetCategoryProjections.stream()
                                                                          .map(SimpleBudgetCategoryDO::categoryId)
                                                                          .collect(Collectors.toSet()));

        var expensesByCategory = expenses.stream().collect(Collectors.groupingBy(ExpenseWithCategoryDO::getCategoryId));

        List<NewBudgetCategoryRequest> result = new ArrayList<>(budgetCategoryProjections.size());
        for (SimpleBudgetCategoryDO budget : budgetCategoryProjections) {
            Optional<CategoryDO> matchingCategoryWithoutBudget = categoriesWithDefaultAmount.stream()
                                                                                            .filter(cwda -> cwda.id() == budget.category().id())
                                                                                            .findAny(); // There should only be one
            result.add(createNewCategoryRequest(matchingCategoryWithoutBudget,
                                                budget,
                                                expensesByCategory.getOrDefault(budget.categoryId(),
                                                                                  Collections.emptyList())));
        }

        this.budgetCategoryDAO.saveAll(result, month);
    }

    private NewBudgetCategoryRequest createNewCategoryRequest(Optional<CategoryDO> c,
                                                              SimpleBudgetCategoryDO pastMonthBudget,
                                                              List<ExpenseWithCategoryDO> expenses) {
        var totalSpentLastMonth = expenses.stream()
                                          .map(ExpenseWithCategoryDO::amount)
                                          .reduce(BigDecimal.ZERO, BigDecimal::add);
        var amountInherited = pastMonthBudget.amountInherited()
                                             .add(pastMonthBudget.amountAssigned())
                                             .subtract(totalSpentLastMonth);
        var assignedAmount = c.map(CategoryDO::defaultAmount)
                              .orElse(pastMonthBudget.amountAssigned());

        return new NewBudgetCategoryRequest(pastMonthBudget.category(),
                                            amountInherited,
                                            assignedAmount);
    }

}
