package com.ivanrl.yaet.yaetApp.domain.expense;


import com.ivanrl.yaet.yaetApp.domain.budget.persistence.BudgetCategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ManageExpensesUseCase {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    
    public ExpenseDO addExpense(NewExpenseRequest newExpenseRequest) {

        ExpensePO newPO = new ExpensePO(categoryRepository.getReferenceById(newExpenseRequest.categoryId()), newExpenseRequest.payee(), newExpenseRequest.amount(), newExpenseRequest.date(), newExpenseRequest.comment());
        this.expenseRepository.saveAndFlush(newPO); // Need to immediately persist to db

        // Extend expense to future budgets
        this.budgetCategoryRepository.updateBudgetCategoryAmount(newExpenseRequest.categoryId(),
                                                                 YearMonth.from(newExpenseRequest.date()),
                                                                 newExpenseRequest.amount().negate());

        return ExpenseDO.from(newPO);
    }
}
