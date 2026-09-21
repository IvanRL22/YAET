package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.YearMonthIntegerAttributeConverter;
import com.ivanrl.yaet.domain.budget.NewBudgetCategoryRequest;
import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.persistence.auth.UserPO;
import com.ivanrl.yaet.persistence.category.CategoryPO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.YearMonth;

import static com.ivanrl.yaet.persistence.UserFilterAspect.USER_FILTER_NAME;


@Filter(name = USER_FILTER_NAME)
@Entity(name = "budgetCategory")
@Table(name = "budget_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BudgetCategoryPO {

    public BudgetCategoryPO(UserPO user,
                            CategoryPO category,
                            YearMonth month,
                            BigDecimal amountInherited,
                            BigDecimal amountAssigned) {
        this.user = user;
        this.category = category;
        this.month = month;
        this.amountInherited = amountInherited;
        this.amountAssigned = amountAssigned;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserPO user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryPO category;

    // 'month' seems to be a reserved word in postgres
    @Column(name = "budget_month", precision = 6, nullable = false)
    @Convert(converter = YearMonthIntegerAttributeConverter.class)
    private YearMonth month;

    @Column(name = "inherited", precision = 6, scale = 2, nullable = false)
    private BigDecimal amountInherited;

    @Column(name = "assigned", precision = 6, scale = 2, nullable = false)
    private BigDecimal amountAssigned;

    public static BudgetCategoryPO from(UserPO user,
                                        NewBudgetCategoryRequest domainObject,
                                        CategoryPO categoryPO,
                                        YearMonth month) {
        return new BudgetCategoryPO(user,
                                    categoryPO,
                                    month,
                                    domainObject.amountInherited(),
                                    domainObject.amountAssigned());
    }

    public SimpleBudgetCategoryDO toSimpleDomainModel() {
        return new SimpleBudgetCategoryDO(this.getId(),
                                    this.category.toSimpleDomainModel(),
                                    this.month,
                                    this.amountInherited,
                                    this.amountAssigned);
    }
}

