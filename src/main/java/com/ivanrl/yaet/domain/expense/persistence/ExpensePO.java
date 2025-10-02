package com.ivanrl.yaet.domain.expense.persistence;


import com.ivanrl.yaet.domain.category.persistence.CategoryPO;
import com.ivanrl.yaet.domain.expense.ExpenseDO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "expenses")
@Table(name = "expenses")
@NoArgsConstructor
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
}
