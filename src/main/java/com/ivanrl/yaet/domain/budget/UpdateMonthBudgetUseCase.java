package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.persistence.budget.BudgetCategoryDAO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class UpdateMonthBudgetUseCase {

    private final ExpenseDAO expenseDAO;
    private final BudgetCategoryDAO budgetCategoryDAO;

    @Transactional
    public void createMonthBudget(YearMonth month,
                                  int categoryId,
                                  BigDecimal amount) {

        YearMonth previousMonth = month.minusMonths(1);
        var previousBudgetCategory = this.budgetCategoryDAO.findBy(previousMonth,
                                                                   categoryId);

        BigDecimal balanceFromLastMonth = previousBudgetCategory.map(balance -> getMonthBalance(balance, previousMonth))
                                                                .orElse(BigDecimal.ZERO);

        this.budgetCategoryDAO.create(categoryId,
                                      month,
                                      balanceFromLastMonth,
                                      amount);

    }

    @Transactional
    public void setBudgetAmount(YearMonth month,
                                int categoryId,
                                BigDecimal amount) {
        // It feels odd to do it like this, should I bring the object to the domain first?
        var diffenceInAmount = this.budgetCategoryDAO.assignAmount(month,
                                                                   categoryId,
                                                                   amount);

        // Update future budgets (at most 1 for now)
        this.budgetCategoryDAO.updateCurrentAndFutureBudgetCategories(categoryId,
                                                                      month.plusMonths(1),
                                                                      diffenceInAmount);
    }

    private BigDecimal getMonthBalance(SimpleBudgetCategoryDO previousBudgetCategory, YearMonth month) {
        var categoryExpenses = this.expenseDAO.findAllBy(month,
                                                           previousBudgetCategory.categoryId());

        BigDecimal totalMonthExpenses = categoryExpenses.expenses()
                                                        .stream()
                                                        .map(ExpenseDO::amount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return previousBudgetCategory.amountInherited()
                                     .add(previousBudgetCategory.amountAssigned())
                                     .subtract(totalMonthExpenses);
    }
}
