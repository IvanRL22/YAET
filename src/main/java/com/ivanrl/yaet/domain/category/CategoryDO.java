package com.ivanrl.yaet.domain.category;

import com.ivanrl.yaet.domain.DomainModel;

@DomainModel
public record CategoryDO(int id, String name, String description) {}
