package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;

public interface BudgetCategoryProjection extends Comparable<String> {


    Integer getId();
    Integer getCategoryId();
    BigDecimal getAmountInherited();
    BigDecimal getAmountAssigned();
    String getName();

    default SimpleBudgetCategoryDO toDomainModel() {
        return new SimpleBudgetCategoryDO(this.getId(),
                                          new SimpleCategoryDO(this.getCategoryId(), this.getName()),
                                          this.getAmountInherited(),
                                          this.getAmountAssigned());
    }

    @Override
    default int compareTo(String o) {
        return getName().compareTo(o);
    }
}
