package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.YearMonthIntegerAttributeConverter;
import com.ivanrl.yaet.domain.budget.NewBudgetCategoryRequest;
import com.ivanrl.yaet.domain.budget.SimpleBudgetCategoryDO;
import com.ivanrl.yaet.persistence.category.CategoryPO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity(name = "budgetCategory")
@Table(name = "budget_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class BudgetCategoryPO {

    public BudgetCategoryPO(CategoryPO category,
                            YearMonth month,
                            BigDecimal amountInherited,
                            BigDecimal amountAssigned) {
        this.category = category;
        this.month = month;
        this.amountInherited = amountInherited;
        this.amountAssigned = amountAssigned;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryPO category;

    // 'month' seems to be a reserved word in postgres
    @Column(name = "budget_month", scale = 6, nullable = false)
    @Convert(converter = YearMonthIntegerAttributeConverter.class)
    private YearMonth month;

    @Column(name = "inherited", scale = 6, precision = 2, nullable = false)
    private BigDecimal amountInherited;

    @Column(name = "assigned", scale = 6, precision = 2, nullable = false)
    private BigDecimal amountAssigned;

    public static BudgetCategoryPO from(NewBudgetCategoryRequest domainObject,
                                        CategoryPO categoryPO,
                                        YearMonth month) {
        return new BudgetCategoryPO(categoryPO,
                                    month,
                                    domainObject.amountInherited(),
                                    domainObject.amountAssigned());
    }

    public SimpleBudgetCategoryDO toSimpleDomainModel() {
        return new SimpleBudgetCategoryDO(this.getId(),
                                    this.category.toSimpleDomainModel(),
                                    this.amountInherited,
                                    this.amountAssigned);
    }
}

