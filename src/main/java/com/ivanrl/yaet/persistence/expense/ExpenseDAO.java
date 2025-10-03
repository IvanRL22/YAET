package com.ivanrl.yaet.persistence.expense;

import com.ivanrl.yaet.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.domain.expense.persistence.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExpenseDAO {

    private final ExpenseRepository repository;
    private final CategoryRepository categoryRepository;

    public ExpenseDO create(NewExpenseRequest request) {
        var categoryReference = this.categoryRepository.getReferenceById(request.categoryId());
        var persisted = this.repository.save(new ExpensePO(categoryReference,
                                                           request.payee(),
                                                           request.amount(),
                                                           request.date(),
                                                           request.comment()));

        return persisted.toDomainModel();
    }


}
