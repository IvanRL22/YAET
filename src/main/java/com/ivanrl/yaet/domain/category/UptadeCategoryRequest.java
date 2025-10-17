package com.ivanrl.yaet.domain.category;

import java.math.BigDecimal;

public record UptadeCategoryRequest(Integer id,
                                    String name,
                                    String description,
                                    BigDecimal defaultAmount) {
}
