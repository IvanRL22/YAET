package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.CategoryDO;

public record CategoryTO(int id,
                         String name,
                         String description) {

    static CategoryTO from(CategoryDO domainObject) {
        return new CategoryTO(domainObject.id(),
                              domainObject.name(),
                              domainObject.description());
    }
}
