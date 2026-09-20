package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryType;

public class CategoryBuilder {

    public static CategoryPO aNormalCategory() {
        return new CategoryPO("Some category", "A demo category", CategoryType.NORMAL);
    }
}
