package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;

public record CategoryTO(int id,
                         String name,
                         String description) implements Comparable<CategoryTO> {

    static CategoryTO from(CategoryDO domainObject) {
        return new CategoryTO(domainObject.id(),
                              domainObject.name(),
                              domainObject.description());
    }

    @Override
    public int compareTo(CategoryTO category) {
        return this.name.compareTo(category.name);
    }
}
