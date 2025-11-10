package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.domain.category.CategoryType;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;

// TODO Consider moving this to a record for clarity
public interface BudgetCategoryProjection extends Comparable<String> {


    Integer getId();
    Integer getCategoryId();
    BigDecimal getAmountInherited();
    BigDecimal getAmountAssigned();
    String getName();
    int getOrder();
    CategoryType getType();

    default SimpleBudgetCategoryDO toDomainModel() {
        return new SimpleBudgetCategoryDO(this.getId(),
                                          new SimpleCategoryDO(this.getCategoryId(),
                                                               this.getName(),
                                                               this.getOrder(),
                                                               this.getType()),
                                          this.getAmountInherited(),
                                          this.getAmountAssigned());
    }

    @Override
    default int compareTo(String o) {
        return getName().compareTo(o);
    }
}
