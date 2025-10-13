package com.ivanrl.yaet.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryPO, Integer> {

    boolean existsByName(String name);
}
