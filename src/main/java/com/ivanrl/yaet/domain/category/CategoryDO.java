package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.category.persistence.CategoryPO;

public record CategoryDO(int id, String name, String description) {

    public static CategoryDO from(CategoryPO po) {
        return new CategoryDO(po.getId(), po.getName(), po.getDescription());
    }
}
