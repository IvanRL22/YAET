package com.ivanrl.yaet.domain.category.domain;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.persistence.category.CategoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeeCategoriesUseCase {

    private final CategoryDAO categoryDAO;

    public List<CategoryDO> getAll() {
        return this.categoryDAO.getAll();
    }
}
