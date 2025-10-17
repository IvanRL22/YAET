package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.CreateCategoryRequest;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "categories")
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class CategoryPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "default_amount", scale = 6, precision = 2)
    private BigDecimal defaultAmount;

    public CategoryPO(CreateCategoryRequest createRequest) {
        this.name = createRequest.name();
        this.description = createRequest.description();
    }

    public CategoryDO toDomainModel() {
        return new CategoryDO(id,
                              name,
                              description,
                              defaultAmount);
    }

    public SimpleCategoryDO toSimpleDomainModel() {
        return  new SimpleCategoryDO(id, name);
    }
}