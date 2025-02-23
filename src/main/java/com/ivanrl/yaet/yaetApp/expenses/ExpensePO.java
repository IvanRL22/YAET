package com.ivanrl.yaet.yaetApp.expenses;


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

    @Column(name = "category", length = 25, nullable = false)
    private String category;

    @Column(name = "payee", length = 50)
    private String payee;

    @Column(name = "amount", scale = 6, precision = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    public ExpensePO(String category, String payee, BigDecimal amount, LocalDate date) {
        this.category = category;
        this.payee = payee;
        this.amount = amount;
        this.date = date;
    }
}
