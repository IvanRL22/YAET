package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.CategoryExpensesDO;
import com.ivanrl.yaet.domain.category.persistence.CategoryPO;
import com.ivanrl.yaet.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.domain.expense.persistence.ExpenseRepository;
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

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public CategoryExpensesDO getExpenses(int categoryId,
                                          YearMonth month) {
        var from = month.atDay(1);
        var to = month.atEndOfMonth();
        CategoryPO categoryPO = categoryRepository.findById(categoryId).orElseThrow(); // TODO - Create specific exception
        var expenses = expenseRepository.findAllByCategoryAndDateBetween(categoryId, from, to)
                                        .stream()
                                        .map(ExpensePO::toDomainModel)
                                        .toList();

        return new CategoryExpensesDO(categoryPO.toDomainModel(),
                                      expenses);
    }

    public Page<ExpensePO> getLastExpenses() {
        return this.expenseRepository.findLastExpenses(Pageable.ofSize(10));
    }

    public Page<ExpensePO> getExpenses(Pageable pageable) {
        return this.expenseRepository.findLastExpenses(pageable);
    }
}
