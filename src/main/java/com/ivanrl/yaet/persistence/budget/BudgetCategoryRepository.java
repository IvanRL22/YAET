package com.ivanrl.yaet.persistence.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Set;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategoryPO, Integer> {


    @Query("""
            select bc.id as id,
            c.id as categoryId,
            bc.amountInherited as amountInherited,
            bc.amountAssigned as amountAssigned,
            c.name as name
            from budgetCategory bc
            right join bc.category c
            where bc.month = :month
            order by c.name
            """)
    Set<BudgetCategoryProjection> findAll(@Param("month") YearMonth month);

    @Query("""
            select bc.id as id,
            c.id as categoryId,
            bc.amountInherited as amountInherited,
            bc.amountAssigned as amountAssigned,
            c.name as name
            from budgetCategory bc
            right join bc.category c
            where bc.month = :month
            and c.id in (:categoryIds)
            order by c.name
            """)
    Set<BudgetCategoryProjection> findAll(YearMonth month, Set<Integer> categoryIds);

    Optional<BudgetCategoryPO> findByCategoryIdAndMonth(int categoryId, YearMonth month);

    /**
     * Adjusts current and future budgets with the amount
     * @param categoryId Identifier of the category
     * @param month Start of the adjustment. This month and futures ones will be adjusted
     * @param amount Amount to be added to the budgets. Can be negative.
     */
    @Modifying
    @Query("""
            update budgetCategory bc
            set bc.amountInherited = bc.amountInherited + :amount
            where bc.category.id = :categoryId
            and bc.month >= :month
            """)
    void updateCurrentAndFutureBudgetCategories(int categoryId, YearMonth month, BigDecimal amount);
}
