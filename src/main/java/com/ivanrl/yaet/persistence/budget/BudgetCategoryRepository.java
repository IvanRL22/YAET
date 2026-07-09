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
            from budgetCategory bc
            right join fetch bc.category c
            where bc.month = :month
            order by c.name
            """)
    Set<BudgetCategoryPO> findAll(@Param("month") YearMonth month);

    @Query("""
            from budgetCategory bc
            right join fetch bc.category c
            where bc.month = :month
            and c.id in (:categoryIds)
            order by c.name
            """)
    Set<BudgetCategoryPO> findAll(YearMonth month, Set<Integer> categoryIds);

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

    /**
     * @param month cutoff month, no budgets for months after this one will be considered
     * @param categoryIds set of category IDs
     * @return Set of budgets for all the categories that are the last with respect to the cutoff month
     */
    @Query("""
            SELECT bc
            FROM budgetCategory bc
            WHERE bc.month <= month
            AND bc.category.id IN (:categoryIds)
            AND bc.month = (
                SELECT MAX(bc2.month)
                FROM budgetCategory bc2
                WHERE bc2.category.id = bc.category.id
            )""")
    Set<BudgetCategoryPO> findLatestBudget(YearMonth month, Set<Integer> categoryIds);

}
