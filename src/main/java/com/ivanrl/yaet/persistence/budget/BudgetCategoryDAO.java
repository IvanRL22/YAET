package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.domain.budget.BudgetCategoryDO;
import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.persistence.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
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

    public Optional<SimpleBudgetCategoryDO> findBy(YearMonth month,
                                                   int categoryId) {
        return this.repository.findByCategoryIdAndMonth(categoryId,
                                                        month)
                              .map(BudgetCategoryPO::toSimpleDomainModel);

    }

    public void updateBudgetCategoryWithNewExpense(NewExpenseRequest request) { // Does it make sense to use the request?
        this.repository.updateCurrentAndFutureBudgetCategories(request.categoryId(),
                                                               YearMonth.from(request.date()).plusMonths(1),
                                                               request.amount().negate()); // Amount needs to be negated so it is subtracted
    }

    public void saveAll(List<BudgetCategoryDO> result, YearMonth month) {
        this.repository.saveAll(result.stream().map(bc -> this.map(bc, month)).toList());
    }

    private BudgetCategoryPO map(BudgetCategoryDO domainObject, YearMonth month) {
        var categoryPO = this.categoryRepository.getReferenceById(domainObject.category().id());
        return BudgetCategoryPO.from(domainObject, categoryPO, month);
    }

    public void create(int categoryId,
                       YearMonth month,
                       BigDecimal balanceFromLastMonth,
                       BigDecimal amount) {
        var po = new BudgetCategoryPO(this.categoryRepository.getReferenceById(categoryId),
                                      month,
                                      balanceFromLastMonth,
                                      amount);
        this.repository.save(po);
    }

    public BigDecimal assignAmount(YearMonth month,
                                   int categoryId,
                                   BigDecimal amount) {

        var po = this.repository.findByCategoryIdAndMonth(categoryId,
                                                          month)
                .orElseThrow();

        var difference = amount.subtract(po.getAmountAssigned());
        po.setAmountAssigned(amount);
        this.repository.save(po);

        return difference;
    }

    public void updateCurrentAndFutureBudgetCategories(int categoryId,
                                                       YearMonth month,
                                                       BigDecimal diffenceInAmount) {
        this.repository.updateCurrentAndFutureBudgetCategories(categoryId,
                                                               month,
                                                               diffenceInAmount);
    }
}
