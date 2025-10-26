package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;

public record CategoryTO(int id,
                         String name,
                         String description,
                         int order) implements Comparable<CategoryTO> {

    static CategoryTO from(CategoryDO domainObject) {
        return new CategoryTO(domainObject.id(),
                              domainObject.name(),
                              domainObject.description(),
                              domainObject.order());
    }

    @Override
    public int compareTo(CategoryTO category) {
        return this.order - category.order;
    }
}
