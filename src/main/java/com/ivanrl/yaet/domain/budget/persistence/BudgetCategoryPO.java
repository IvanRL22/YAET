package com.ivanrl.yaet.domain.budget.persistence;

import com.ivanrl.yaet.YearMonthIntegerAttributeConverter;
import com.ivanrl.yaet.domain.category.persistence.CategoryPO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity(name = "budgetCategory")
@Table(name = "budget_categories")
@NoArgsConstructor
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

}

