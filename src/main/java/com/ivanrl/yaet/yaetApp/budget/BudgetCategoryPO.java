package com.ivanrl.yaet.yaetApp.budget;

import com.ivanrl.yaet.yaetApp.YearMonthIntegerAttributeConverter;
import com.ivanrl.yaet.yaetApp.expenses.CategoryPO;
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
                            BigDecimal amountAssigned) {
        this.category = category;
        this.month = month;
        this.amountAssigned = amountAssigned;
        this.amountInherited = BigDecimal.ZERO;
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

