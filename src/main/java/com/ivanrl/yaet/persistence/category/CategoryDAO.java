package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.UptadeCategoryRequest;
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

    public CategoryDO getById(int id) {
        return this.repository.findById(id).map(CategoryPO::toDomainModel).orElseThrow();
    }

    public CategoryDO update(UptadeCategoryRequest request) {
        var po = this.repository.findById(request.id()).orElseThrow();

        po.setName(request.name());
        po.setDescription(request.description());

        this.repository.save(po);

        return po.toDomainModel();
    }
}
