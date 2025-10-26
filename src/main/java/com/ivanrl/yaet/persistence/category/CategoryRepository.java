package com.ivanrl.yaet.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<CategoryPO, Integer> {

    boolean existsByName(String name);

    @Modifying
    @Query("""
            UPDATE categories
            SET order = order + :increment
            WHERE order BETWEEN :from AND :to
            """)
    void adjustOrder(int from, int to, int increment);

    @Modifying
    @Query("""
            UPDATE categories
            SET order = :newPosition
            WHERE id = :id
            """)
    void setOrder(int id, int newPosition);
}
