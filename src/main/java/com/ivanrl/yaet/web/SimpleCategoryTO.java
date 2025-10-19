package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;

public record SimpleCategoryTO(int id,
                               String name) implements Comparable<SimpleCategoryTO>{

    public static SimpleCategoryTO from(CategoryDO domainObject) {
        return new SimpleCategoryTO(domainObject.id(),
                                    domainObject.name());
    }

    @Override
    public int compareTo(SimpleCategoryTO other) {
        return this.name.compareTo(other.name);
    }
}
