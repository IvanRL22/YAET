package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.CreateCategoryRequest;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
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

    public List<SimpleCategoryDO> getAllSimple() {
        return this.repository.findAll().stream()
                              .map(CategoryPO::toSimpleDomainModel)
                              .toList();
    }

    public CategoryDO getById(int id) {
        return this.repository.findById(id).map(CategoryPO::toDomainModel).orElseThrow();
    }

    public CategoryDO create(CreateCategoryRequest request) {
        var po = new CategoryPO(request);
        po.setOrder((int) (this.repository.count() + 1));

        this.repository.save(po);

        return po.toDomainModel();
    }

    public CategoryDO update(UptadeCategoryRequest request) {
        var po = this.repository.findById(request.id()).orElseThrow();

        po.setName(request.name());
        po.setDescription(request.description());
        po.setDefaultAmount(request.defaultAmount());

        this.repository.save(po);

        return po.toDomainModel();
    }

    public boolean existsByName(CreateCategoryRequest request) {
        return this.repository.existsByName(request.name());
    }

    public void adjustOrder(int from, int to, int increment) {
        this.repository.adjustOrder(from, to, increment);
    }

    public void setOrder(int id, int newPosition) {
        this.repository.setOrder(id, newPosition);
    }
}
