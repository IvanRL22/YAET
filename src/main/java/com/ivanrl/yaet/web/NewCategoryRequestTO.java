package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryType;
import com.ivanrl.yaet.domain.category.CreateCategoryRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.logging.log4j.util.Strings;

public record NewCategoryRequestTO(@NotEmpty @Size(max = 50) String name,
                                   @Size(max = 255)
                                   String description,
                                   @NotNull(message = "A type of category must be selected")
                                   CategoryType type) {

    public static NewCategoryRequestTO empty() {
        return new NewCategoryRequestTO(Strings.EMPTY,
                                        Strings.EMPTY,
                                        null);
    }

    public CreateCategoryRequest toDomain() {
        return new CreateCategoryRequest(name, description, type);
    }
}
