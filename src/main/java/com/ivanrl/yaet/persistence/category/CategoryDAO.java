package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryDAO {

    private final CategoryRepository repository;

    public List<CategoryDO> getAll() {
        return this.repository.findAll().stream()
                              .map(CategoryPO::toDomainModel)
                              .toList();
    }


}
