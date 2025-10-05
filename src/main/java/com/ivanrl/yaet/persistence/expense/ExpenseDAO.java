package com.ivanrl.yaet.persistence.expense;

import com.ivanrl.yaet.domain.expense.CategoryExpensesDO;
import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.persistence.category.CategoryPO;
import com.ivanrl.yaet.persistence.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

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

    public List<ExpenseWithCategoryDO> findAllBy(YearMonth month) {
        return this.repository.findAllByDateBetween(month.atDay(1),
                                                    month.atEndOfMonth())
                              .stream()
                              .map(ExpensePO::toDomainModelWithCategory)
                              .toList();
    }

    public CategoryExpensesDO findAllBy(YearMonth month,
                                        int categoryId) {
        var peristenceExpenses = repository.findAllWithCategoryByCategoryAndDateBetween(categoryId,
                                                                              month.atDay(1),
                                                                              month.atEndOfMonth());

        CategoryPO categoryPO;
        if (peristenceExpenses.isEmpty()) {
            categoryPO = this.categoryRepository.findById(categoryId)
                                                .orElseThrow(); // TODO Need to define proper persistence exceptions
        } else {
            categoryPO = peristenceExpenses.get(0).getCategory();
        }

        return new CategoryExpensesDO(categoryPO.toDomainModel(),
                                      peristenceExpenses.stream()
                                                        .map(ExpensePO::toDomainModel)
                                                        .toList());
    }

    public List<ExpenseWithCategoryDO> findAllBy(YearMonth month,
                                                 Set<Integer> categoryIds) {
        return this.repository.findAllWithCategory(month.atDay(1),
                                                   month.atEndOfMonth(),
                                                   categoryIds)
                              .stream()
                              .map(ExpensePO::toDomainModelWithCategory)
                              .toList();
    }

    public Page<ExpenseDO> getLastExpenses(Pageable pageable) {
        return this.repository.findLastExpenses(pageable)
                              .map(ExpensePO::toDomainModel);
    }


    public ExpenseWithCategoryDO findBy(int id) {
        return this.repository.findById(id)
                .map(ExpensePO::toDomainModelWithCategory)
                .orElseThrow();// TODO Create proper exception
    }
}
