package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.ValidationError;
import com.ivanrl.yaet.persistence.category.CategoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageCategoriesUseCase {

    private final CategoryDAO categoryDAO;

    public CategoryDO update(UptadeCategoryRequest request) {
        return this.categoryDAO.update(request);
    }

    public CategoryDO create(CreateCategoryRequest request) {
        if (this.categoryDAO.existsByName(request)) {
            throw new ValidationError("A category with that name already exists");
        }

        return this.categoryDAO.create(request);
    }
}
