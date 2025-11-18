package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.CategoryType;
import com.ivanrl.yaet.domain.category.UptadeCategoryRequest;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateCategoryRequestTO(@NotNull(message = "There is an issue when identifying this category")
                                      Integer id,
                                      @NotEmpty(message = "Name must not be empty")
                                      @Size(max = 50, message = "Name cannot exceed 50 characters")
                                      String name,
                                      @Size(max = 255, message = "Description must not exceed 255 characters")
                                      String description,
                                      CategoryType type,
                                      @Positive(message = "Default amount cannot be negative")
                                      @DecimalMax(value = "9999.99", message = "Amount must be less than 10000")
                                      BigDecimal defaultAmount) {

    static UpdateCategoryRequestTO from(CategoryDO domainObject) {
        return new UpdateCategoryRequestTO(domainObject.id(),
                                           domainObject.name(),
                                           domainObject.description(),
                                           domainObject.type(),
                                           domainObject.defaultAmount());
    }

    UptadeCategoryRequest toDomainModel() {
        return new UptadeCategoryRequest(id, name, description, defaultAmount);
    }

}
