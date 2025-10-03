package com.ivanrl.yaet.domain.budget;

import com.ivanrl.yaet.domain.category.CategoryDO;

import java.math.BigDecimal;

public interface BudgetCategoryProjection extends Comparable<String> {


    Integer getId();
    Integer getCategoryId();
    BigDecimal getAmountInherited();
    BigDecimal getAmountAssigned();
    String getName();

    default SimpleBudgetCategoryDO toDomainModel() {
        return new SimpleBudgetCategoryDO(this.getId(),
                                          new CategoryDO(this.getCategoryId(), this.getName(), null),
                                          this.getAmountInherited(),
                                          this.getAmountAssigned());
    }

    @Override
    default int compareTo(String o) {
        return getName().compareTo(o);
    }
}
