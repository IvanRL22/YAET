package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.ValidationError;
import com.ivanrl.yaet.persistence.category.CategoryDAO;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void reorder(int id, int newPosition) {
        var category = this.categoryDAO.getById(id);
        var oldPosition = category.order();
        var increment = newPosition > oldPosition ? -1 : 1;
        // Bit of an ugly way to determine from and to
        int from = Math.min(oldPosition, newPosition);
        if (from == oldPosition) {
            from++;
        }
        int to = Math.max(oldPosition, newPosition);
        if (to == oldPosition) {
            to--;
        }
        this.categoryDAO.adjustOrder(from,
                                     to,
                                     increment);
        this.categoryDAO.setOrder(id, newPosition);
    }
}
