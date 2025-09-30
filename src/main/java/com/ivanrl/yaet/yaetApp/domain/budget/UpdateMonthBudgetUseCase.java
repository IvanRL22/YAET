package com.ivanrl.yaet.yaetApp.domain.budget;

import com.ivanrl.yaet.yaetApp.budget.BudgetCategoryPO;
import com.ivanrl.yaet.yaetApp.budget.BudgetCategoryRepository;
import com.ivanrl.yaet.yaetApp.expenses.CategoryRepository;
import com.ivanrl.yaet.yaetApp.expenses.ExpensePO;
import com.ivanrl.yaet.yaetApp.expenses.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class UpdateMonthBudgetUseCase {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public void createMonthBudget(YearMonth month,
                             int categoryId,
                             BigDecimal amount) {

        YearMonth previousMonth = month.minusMonths(1);
        var previousBudgetCategory = this.budgetCategoryRepository.findByCategoryIdAndMonth(categoryId,
                                                                                            previousMonth);

        BigDecimal balanceFromLastMonth = previousBudgetCategory.map(balance -> getMonthBalance(balance, previousMonth))
                                                                .orElse(BigDecimal.ZERO);

        var po = new BudgetCategoryPO(this.categoryRepository.getReferenceById(categoryId),
                                      month,
                                      balanceFromLastMonth,
                                      amount);
        this.budgetCategoryRepository.save(po);

    }

    @Transactional
    public void setBudgetAmount(YearMonth month,
                                int categoryId,
                                BigDecimal amount) {

        var po = budgetCategoryRepository.findByCategoryIdAndMonth(categoryId, month)
                                         .orElseThrow(); // TODO Handle - Need to decide how this should look in the frontend
        var differenceInAmountAssigned = amount.subtract(po.getAmountAssigned());
        po.setAmountAssigned(amount);

        // Update future budgets (at most 1 for now)
        budgetCategoryRepository.updateBudgetCategoryAmount(categoryId,
                                                            month,
                                                            differenceInAmountAssigned);
    }

    private BigDecimal getMonthBalance(BudgetCategoryPO previousBudgetCategory, YearMonth month) {
        var expenses = this.expenseRepository.findAllByCategoryAndDateBetween(previousBudgetCategory.getCategory().getId(),
                                                                              month.atDay(1),
                                                                              month.atEndOfMonth());

        BigDecimal totalMonthExpenses = expenses.stream()
                                                .map(ExpensePO::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return previousBudgetCategory.getAmountInherited()
                                     .add(previousBudgetCategory.getAmountAssigned())
                                     .subtract(totalMonthExpenses);
    }
}
