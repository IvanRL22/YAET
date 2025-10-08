package com.ivanrl.yaet.domain.expense;


import com.ivanrl.yaet.BadRequestException;
import com.ivanrl.yaet.persistence.budget.BudgetCategoryDAO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ManageExpensesUseCase {

    private final ExpenseDAO expenseDAO;
    private final BudgetCategoryDAO budgetCategoryDAO;
    
    public ExpenseDO addExpense(NewExpenseRequest newExpenseRequest) {
        var newExpense = this.expenseDAO.create(newExpenseRequest);

        // Extend expense to future budgets
        this.budgetCategoryDAO.updateBudgetCategoryWithNewExpense(newExpenseRequest);

        return newExpense;
    }

    @Transactional
    public void updateExpense(UpdateExpenseRequest request) {
        var old = this.expenseDAO.findBy(request.id());
        if (!old.date().getMonth().equals(request.date().getMonth())) {
            throw new BadRequestException("Cannot change the date of an expense to a different month");
        }
        var differenceInAmount = old.amount().subtract(request.amount()); // Subtracting from old to get the right sign

        this.expenseDAO.update(request);

        if (!differenceInAmount.equals(BigDecimal.ZERO)) {
            this.budgetCategoryDAO.updateCurrentAndFutureBudgetCategories(old.getCategoryId(),
                                                                          YearMonth.from(request.date().plusMonths(1)),
                                                                          differenceInAmount);
        }
    }
}
