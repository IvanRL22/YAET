package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.DomainModel;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;

import java.math.BigDecimal;
import java.time.LocalDate;

@DomainModel
public record ExpenseWithCategoryDO(int id,
                                    SimpleCategoryDO category,
                                    String payee,
                                    LocalDate date,
                                    BigDecimal amount,
                                    String comment) {

    public int getCategoryId() {
        return category.id();
    }
}
