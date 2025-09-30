package com.ivanrl.yaet.yaetApp.domain.category;

import com.ivanrl.yaet.yaetApp.expenses.CategoryPO;

public record CategoryDO(int id, String name, String description) {

    public static CategoryDO from(CategoryPO po) {
        return new CategoryDO(po.getId(), po.getName(), po.getDescription());
    }
}
