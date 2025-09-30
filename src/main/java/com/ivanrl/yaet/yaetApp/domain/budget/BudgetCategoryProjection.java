package com.ivanrl.yaet.yaetApp.domain.budget;

import java.math.BigDecimal;

public interface BudgetCategoryProjection extends Comparable<String> {


    Integer getId();
    Integer getCategoryId();
    BigDecimal getAmountInherited();
    BigDecimal getAmountAssigned();
    String getName();

    @Override
    default int compareTo(String o) {
        return getName().compareTo(o);
    }
}
