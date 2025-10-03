package com.ivanrl.yaet.domain.expense;


import com.ivanrl.yaet.domain.budget.persistence.BudgetCategoryRepository;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ManageExpensesUseCase {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final ExpenseDAO expenseDAO;
    
    public ExpenseDO addExpense(NewExpenseRequest newExpenseRequest) {
        var newExpense = this.expenseDAO.create(newExpenseRequest);

        // Extend expense to future budgets
        // TODO Parameters should be replaced with fields from domain object
        this.budgetCategoryRepository.updateBudgetCategoryAmount(newExpenseRequest.categoryId(),
                                                                 YearMonth.from(newExpenseRequest.date()),
                                                                 newExpenseRequest.amount().negate());

        return newExpense;
    }
}
