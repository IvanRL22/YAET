package com.ivanrl.yaet.domain.expense;


import com.ivanrl.yaet.persistence.budget.BudgetCategoryDAO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
