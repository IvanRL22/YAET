package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.domain.budget.BudgetCategoryProjection;
import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.domain.budget.persistence.BudgetCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BudgetCategoryDAO {

    private final BudgetCategoryRepository repository;

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
}
