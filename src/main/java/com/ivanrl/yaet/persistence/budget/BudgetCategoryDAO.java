package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.domain.budget.BudgetCategoryDO;
import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.persistence.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BudgetCategoryDAO {

    private final BudgetCategoryRepository repository;
    private final CategoryRepository categoryRepository;


    public List<SimpleBudgetCategoryDO> findAllBy(YearMonth month) {
        return this.repository.findAll(month)
                              .stream()
                              .map(BudgetCategoryProjection::toDomainModel)
                              .toList();
    }

    public List<SimpleBudgetCategoryDO>findAllBy(YearMonth month,
                                                 Set<Integer> categoryIds) {
        return this.repository.findAll(month,
                                       categoryIds)
                              .stream()
                              .map(BudgetCategoryProjection::toDomainModel)
                              .toList();

    }

    public void updateBudgetCategoryWithNewExpense(NewExpenseRequest request) { // Does it make sense to use the request?
        this.repository.updateBudgetCategoryAmount(request.categoryId(),
                                                   YearMonth.from(request.date()),
                                                   request.amount());
    }

    public void saveAll(List<BudgetCategoryDO> result, YearMonth month) {
        this.repository.saveAll(result.stream().map(bc -> this.map(bc, month)).toList());
    }

    private BudgetCategoryPO map(BudgetCategoryDO domainObject, YearMonth month) {
        var categoryPO = this.categoryRepository.getReferenceById(domainObject.categoryId());
        return BudgetCategoryPO.from(domainObject, categoryPO, month);
    }
}
