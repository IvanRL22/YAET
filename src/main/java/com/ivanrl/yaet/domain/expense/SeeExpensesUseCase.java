package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.CategoryExpensesDO;
import com.ivanrl.yaet.persistence.expense.ExpenseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeeExpensesUseCase {

    private final ExpenseDAO expenseDAO;

    public CategoryExpensesDO getExpenses(int categoryId,
                                          YearMonth month) {
        return this.expenseDAO.findAllBy(month, categoryId);
    }

    public Page<ExpenseDO> getLastExpenses() {
        return this.expenseDAO.getLastExpenses(Pageable.ofSize(10));
    }

    public Page<ExpenseDO> getLastExpenses(Pageable pageable) {
        return this.expenseDAO.getLastExpenses(pageable);
    }
}
