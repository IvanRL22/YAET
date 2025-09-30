package com.ivanrl.yaet.yaetApp.domain.budget;

import com.ivanrl.yaet.yaetApp.BadRequestException;
import com.ivanrl.yaet.yaetApp.domain.budget.persistence.BudgetCategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryPO;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpenseRepository;
import com.ivanrl.yaet.yaetApp.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeeMonthBudgetUseCase {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    @Transactional(readOnly = true)
    public BudgetMonthDO seeMonthlyBudget(YearMonth requestedMonth) {
        YearMonth lastAvailableMonth = YearMonth.now().plusMonths(1);
        // TODO Should this be a different exception?
        if (requestedMonth.isAfter(lastAvailableMonth)) {
            throw new BadRequestException("You can only see up to the next month.");
        }

        BigDecimal totalIncome = this.incomeRepository.getTotalIncome(requestedMonth.atDay(1),
                                                                      requestedMonth.atEndOfMonth());
        List<BudgetCategoryDO> categories = getCategoriesInformation(requestedMonth);

        return new BudgetMonthDO(totalIncome, categories);
    }

    @Transactional(readOnly = true)
    public List<BudgetCategoryDO> getBudgets(YearMonth requestedMonth) {
        return getCategoriesInformation(requestedMonth);
    }

    private List<BudgetCategoryDO> getCategoriesInformation(YearMonth requestedMonth) {
        Set<BudgetCategoryProjection> categoriesFromCurrentMonth = budgetCategoryRepository.findAll(requestedMonth);
        var currentMonthExpenses = expenseRepository.findAllByDateBetween(requestedMonth.atDay(1),
                                                                          requestedMonth.atEndOfMonth());
        var currentMonthCategories = categoriesFromCurrentMonth.stream()
                                                               .map(c -> createCurrentMonthCategory(c, currentMonthExpenses)).toList();

        // Checking if all categories have a budget for the requested month
        // Having to check this for each time does not seem very performant
        var missingCategories = categoryRepository.findAll();
        missingCategories.removeIf(c -> categoriesFromCurrentMonth.stream()
                                                                  .anyMatch(bcp -> bcp.getCategoryId() == c.getId()));

        if (missingCategories.isEmpty()) {
            return currentMonthCategories;
        }

        // From here, we know there are categories without budget for the requested month

        var categoriesWithoutBudget = getCategoriesWithoutCurrentMonthBudget(requestedMonth, missingCategories);
        List<BudgetCategoryDO> allCategories = new ArrayList<>(currentMonthCategories.size() + missingCategories.size());
        allCategories.addAll(categoriesWithoutBudget);
        allCategories.addAll(currentMonthCategories);
        allCategories.sort(Comparator.comparing(BudgetCategoryDO::name));

        return allCategories;
    }

    private List<BudgetCategoryDO> getCategoriesWithoutCurrentMonthBudget(YearMonth requestedMonth,
                                                                          List<CategoryPO> missingCategories) {
        YearMonth previousMonth = requestedMonth.minusMonths(1);
        // What if some category still does not have a budget for the previous month?
        var categoriesWithoutMonthBudget = budgetCategoryRepository.findAll(previousMonth,
                                                                            missingCategories.stream()
                                                                                             .map(CategoryPO::getId)
                                                                                             .collect(Collectors.toSet()));

        var pastMonthExpenses = expenseRepository.findAllWithCategory(previousMonth.atDay(1),
                                                                      previousMonth.atEndOfMonth(),
                                                                      categoriesWithoutMonthBudget.stream()
                                                                                                  .map(BudgetCategoryProjection::getCategoryId)
                                                                                                  .collect(Collectors.toSet()));

        // Grouping the expenses by category now to avoid having to iterate through all of them when creating each TO
        var pastMonthExpensesByCategory = pastMonthExpenses.stream()
                                                           .collect(Collectors.groupingBy(e -> e.getCategory().getId()));

        return categoriesWithoutMonthBudget.stream()
                                           .map(cwmb -> createCurrentMonthCategoryWithoutBudget(cwmb, pastMonthExpensesByCategory.getOrDefault(cwmb.getCategoryId(),
                                                                                                                                               new ArrayList<>())))
                                           .toList();
    }

    // Should this be a static method on BudgetCategoryTO?
    // The expenses are expected to be just for the budgetCategory
    private BudgetCategoryDO createCurrentMonthCategoryWithoutBudget(BudgetCategoryProjection budgetCategory,
                                                                     List<ExpensePO> pastMonthExpenses) {
        var totalSpent = pastMonthExpenses.stream()
                                          .map(ExpensePO::getAmount)
                                          .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BudgetCategoryDO(budgetCategory.getCategoryId(),
                                    budgetCategory.getName(),
                                    budgetCategory.getAmountInherited().add(budgetCategory.getAmountAssigned()).subtract(totalSpent),
                                    BigDecimal.ZERO,
                                    BigDecimal.ZERO);
    }

    private BudgetCategoryDO createCurrentMonthCategory(BudgetCategoryProjection c,
                                                        List<ExpensePO> expenses) {
        var totalSpentInCategory = expenses.stream()
                                           .filter(e -> e.getCategory().getName().equals(c.getName()))
                                           .map(ExpensePO::getAmount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BudgetCategoryDO.from(c, totalSpentInCategory);
    }

}
