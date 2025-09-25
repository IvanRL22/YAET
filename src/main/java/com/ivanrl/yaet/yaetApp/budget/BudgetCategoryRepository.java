package com.ivanrl.yaet.yaetApp.budget;

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

    @Modifying
    @Query("""
            update budgetCategory bc
            set bc.amountInherited = bc.amountInherited + :differenceInAmountAssigned
            where bc.category.id = :categoryId
            and bc.month > :month
            """)
    void updateBudgetCategoryAmount(int categoryId, YearMonth month, BigDecimal differenceInAmountAssigned);
}
