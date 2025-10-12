package com.ivanrl.yaet.domain.category;

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
}
