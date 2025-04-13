package com.ivanrl.yaet.yaetApp.expenses;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryPO, Integer> {
}
