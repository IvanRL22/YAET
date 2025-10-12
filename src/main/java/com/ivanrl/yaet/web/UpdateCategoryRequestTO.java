package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.UptadeCategoryRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequestTO(@NotNull(message = "There is an issue when identifying this category")
                                      Integer id,
                                      @NotEmpty(message = "Name must not be empty")
                                      @Size(max = 50, message = "Name cannot exceed 50 characters")
                                      String name,
                                      @Size(max = 255, message = "Description must not exceed 255 characters")
                                      String description) {

    static UpdateCategoryRequestTO from(CategoryDO domainObject) {
        return new UpdateCategoryRequestTO(domainObject.id(),
                                           domainObject.name(),
                                           domainObject.description());
    }

    UptadeCategoryRequest toDomainModel() {
        return new UptadeCategoryRequest(id, name, description);
    }

}
