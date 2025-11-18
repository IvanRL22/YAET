package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;

@DomainModel
public record CategoryDO(int id,
                         String name,
                         String description,
                         CategoryType type,
                         BigDecimal defaultAmount,
                         int order) {}
