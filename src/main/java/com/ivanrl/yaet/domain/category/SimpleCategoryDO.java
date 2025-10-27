package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.DomainModel;

/**
 * Represents the minimum useful information of a category.
 * To be used as an attribute of other domain model objects or when the information in this object is enough.
 * TODO Consider if there should be a separate object with order, like an SimpleOrderedCategory
 */
@DomainModel
public record SimpleCategoryDO(int id,
                               String name,
                               int order) {

}
