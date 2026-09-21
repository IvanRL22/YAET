package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.CategoryType;
import com.ivanrl.yaet.domain.category.CreateCategoryRequest;
import com.ivanrl.yaet.domain.category.SimpleCategoryDO;
import com.ivanrl.yaet.persistence.auth.UserPO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

import static com.ivanrl.yaet.persistence.UserFilterAspect.USER_FILTER_NAME;

@Filter(name = USER_FILTER_NAME)
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

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserPO user;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "default_amount", scale = 6, precision = 2)
    private BigDecimal defaultAmount;

    @Column(name = "screen_order", nullable = false, scale = 3) // 'order' is a reserved word in postgres
    private int order;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CategoryType type;

    public CategoryPO(CreateCategoryRequest createRequest, UserPO user) {
        this.user = user;
        this.name = createRequest.name();
        this.description = createRequest.description();
        this.type = createRequest.type();
    }

    CategoryPO(UserPO user, String name, String description, CategoryType type) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public CategoryDO toDomainModel() {
        return new CategoryDO(id,
                              name,
                              description,
                              type,
                              defaultAmount,
                              order);
    }

    public SimpleCategoryDO toSimpleDomainModel() {
        return new SimpleCategoryDO(id,
                                    name,
                                    order,
                                    type);
    }
}