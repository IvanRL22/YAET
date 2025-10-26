package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.DomainModel;

import java.math.BigDecimal;

@DomainModel
public record CategoryDO(int id,
                         String name,
                         String description,
                         BigDecimal defaultAmount,
                         int order) {}
