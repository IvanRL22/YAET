package com.ivanrl.yaet.yaetApp.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategoryPO, Integer> {


    @Query("""
            select bc.id as id,
            bc.amountInherited as amountInherited,
            bc.amountAssigned as amountAssigned,
            c.name as name
            from budgetCategory bc
            right join bc.category c
            where bc.month = :month
            order by c.name
            """)
    Set<BudgetCategoryProjection> findAllWithCategory(@Param("month") int month);

}
