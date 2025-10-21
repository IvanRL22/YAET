package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.BadRequestException;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;
import com.ivanrl.yaet.persistence.budget.BudgetCategoryDAO;
import com.ivanrl.yaet.persistence.category.CategoryDAO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import com.ivanrl.yaet.persistence.income.IncomeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeeMonthBudgetUseCase {

    private final ExpenseDAO expenseDAO;
    private final IncomeDAO incomeDAO;
    private final CategoryDAO categoryDAO;
    private final BudgetCategoryDAO budgetCategoryDAO;

    @Transactional(readOnly = true)
    public BudgetMonthDO seeMonthlyBudget(YearMonth requestedMonth) {
        YearMonth lastAvailableMonth = YearMonth.now().plusMonths(1);
        // TODO Should this be a different exception?
        if (requestedMonth.isAfter(lastAvailableMonth)) {
            throw new BadRequestException("You can only see up to the next month.");
        }

        BigDecimal totalIncome = this.incomeDAO.getTotalIncome(requestedMonth);
        List<BudgetCategoryDO> categories = getCategoriesInformation(requestedMonth);

        return new BudgetMonthDO(totalIncome, categories);
    }

    @Transactional(readOnly = true)
    public List<BudgetCategoryDO> getBudgets(YearMonth requestedMonth) {
        return getCategoriesInformation(requestedMonth);
    }

    private List<BudgetCategoryDO> getCategoriesInformation(YearMonth requestedMonth) {
        var categoriesFromCurrentMonth = this.budgetCategoryDAO.findAllBy(requestedMonth);
        var currentMonthExpenses = this.expenseDAO.findAllBy(requestedMonth);
        var expensesByCategory = currentMonthExpenses.stream()
                                                     .collect(Collectors.groupingBy(ExpenseWithCategoryDO::category));

        var currentMonthCategories = categoriesFromCurrentMonth.stream()
                                                               .map(c -> BudgetCategoryDO.from(c, expensesByCategory.getOrDefault(c.category(),
                                                                                                                                  new ArrayList<>())))
                                                               .toList();

        // Checking if all categories have a budget for the requested month
        // Having to check this for each time does not seem very performant
        var missingCategories = new ArrayList<>(this.categoryDAO.getAllSimple());
        missingCategories.removeIf(c -> categoriesFromCurrentMonth.stream()
                                                                  .anyMatch(bcp -> bcp.category().id() == c.id()));

        if (missingCategories.isEmpty()) {
            return currentMonthCategories;
        }

        // From here, we know there are categories without budget for the requested month

        var categoriesWithoutBudget = getCategoriesWithoutCurrentMonthBudget(requestedMonth, missingCategories);
        List<BudgetCategoryDO> allCategories = new ArrayList<>(currentMonthCategories.size() + missingCategories.size());
        allCategories.addAll(categoriesWithoutBudget);
        allCategories.addAll(currentMonthCategories);
        allCategories.sort(Comparator.comparing(BudgetCategoryDO::getCategoryName));

        return allCategories;
    }

    private List<BudgetCategoryDO> getCategoriesWithoutCurrentMonthBudget(YearMonth requestedMonth,
                                                                          List<SimpleCategoryDO> missingCategories) {
        YearMonth previousMonth = requestedMonth.minusMonths(1);
        // What if some category still does not have a budget for the previous month?
        List<SimpleBudgetCategoryDO> missingBudgetsFromPastMonth = this.budgetCategoryDAO.findAllBy(previousMonth,
                                                                                                     missingCategories.stream()
                                                                                                                      .map(SimpleCategoryDO::id)
                                                                                                                      .collect(Collectors.toSet()));

        // TODO This is not very elegant, there's probably a better way to do this by tweaking the model a bit
        List<SimpleBudgetCategoryDO> categoriesWithoutMonthBudget = new ArrayList<>(missingBudgetsFromPastMonth);
        for (SimpleCategoryDO c : missingCategories) {
            if (categoriesWithoutMonthBudget.stream()
                                            .noneMatch(cwmb -> cwmb.category().equals(c))) {
                categoriesWithoutMonthBudget.add(SimpleBudgetCategoryDO.emptyWith(c));
            }
        }


        List<ExpenseWithCategoryDO> pastMonthExpenses = this.expenseDAO.findAllBy(previousMonth,
                                                                                  categoriesWithoutMonthBudget.stream()
                                                                                                              .map(SimpleBudgetCategoryDO::categoryId)
                                                                                                              .collect(Collectors.toSet()));

        // Grouping the expenses by category now to avoid having to iterate through all of them when creating each TO
        Map<Integer, List<ExpenseWithCategoryDO>> pastMonthExpensesByCategory = pastMonthExpenses.stream()
                                                                                                 .collect(Collectors.groupingBy(e -> e.category().id()));

        return categoriesWithoutMonthBudget.stream()
                                           .map(cwmb -> BudgetCategoryDO.from(cwmb,
                                                                              pastMonthExpensesByCategory.getOrDefault(cwmb.categoryId(),
                                                                                                                       new ArrayList<>())))
                                           .toList();
    }

}
