package com.ivanrl.yaet.domain.expense;

import com.ivanrl.yaet.domain.category.CategoryDO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseWithCategoryDO(int id,
                                    CategoryDO category,
                                    String payee,
                                    LocalDate date,
                                    BigDecimal amount) {
}
