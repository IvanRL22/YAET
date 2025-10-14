package com.ivanrl.yaet.persistence.expense;


import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.domain.expense.ExpenseWithCategoryDO;
import com.ivanrl.yaet.persistence.category.CategoryPO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "expenses")
@Table(name = "expenses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ExpensePO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryPO category;

    @Column(name = "payee", length = 50)
    private String payee;

    @Column(name = "amount", scale = 6, precision = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Column(name = "comment")
    private String comment;

    public ExpensePO(CategoryPO category, String payee, BigDecimal amount, LocalDate date, String comment) {
        this.category = category;
        this.payee = payee;
        this.amount = amount;
        this.date = date;
        this.comment = comment;
    }

    public ExpenseDO toDomainModel() {
        return new ExpenseDO(this.getId(),
                             this.getCategory().getName(),
                             this.getPayee(),
                             this.getAmount(),
                             this.getDate());
    }

    public ExpenseWithCategoryDO toDomainModelWithCategory() {
        return new ExpenseWithCategoryDO(this.getId(),
                                         this.getCategory().toDomainModel(),
                                         this.getPayee(),
                                         this.getDate(),
                                         this.getAmount(),
                                         this.comment);
    }
}
