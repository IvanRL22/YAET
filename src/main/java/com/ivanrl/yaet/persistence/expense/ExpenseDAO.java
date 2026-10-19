package com.ivanrl.yaet.persistence.expense;

import com.ivanrl.yaet.UserData;
import com.ivanrl.yaet.domain.expense.*;
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
    private final UserData userData;

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


    /**
     * TODO: Fix this in a way that avoids returning an expense that does not belong to the user
     * This operation is dangerous: querying db by id ignores user filtering.
     */
    public ExpenseWithCategoryDO findBy(int id) {
        return this.repository.findById(id)
                .map(ExpensePO::toDomainModelWithCategory)
                .orElseThrow();// TODO Create proper exception
    }

    public ExpenseWithCategoryDO update(UpdateExpenseRequest request) {
        var po = this.repository.findById(request.id()).orElseThrow();
        po.setPayee(request.payee());
        po.setDate(request.date());
        po.setAmount(request.amount());
        po.setComment(request.comment());

        this.repository.save(po);
        return po.toDomainModelWithCategory();
    }

    public void remove(int id) {
        var expense = this.repository.findById(id)
                                     .orElseThrow();

        if (expense.getCategory().getUserId() != this.userData.getDbId()) {
            throw new RuntimeException("Expense does not belong to the user. Something fishy is going on.");
        }

        this.repository.delete(expense);
    }
}
