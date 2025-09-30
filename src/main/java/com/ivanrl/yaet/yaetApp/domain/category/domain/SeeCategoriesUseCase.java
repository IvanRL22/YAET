package com.ivanrl.yaet.yaetApp.domain.category.domain;

import com.ivanrl.yaet.yaetApp.domain.category.CategoryDO;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeeCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public List<CategoryDO> getAll() {
        return this.categoryRepository.findAll().stream().map(CategoryDO::from).toList();
    }
}
