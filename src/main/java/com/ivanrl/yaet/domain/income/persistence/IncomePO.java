package com.ivanrl.yaet.domain.income.persistence;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "incomes")
@Table(name = "incomes")
@NoArgsConstructor
@Getter
@Setter
public class IncomePO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "payer", length = 50)
    private String payer;

    @Column(name = "amount", scale = 6, precision = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    public IncomePO(String payer, BigDecimal amount, LocalDate date) {
        this.payer = payer;
        this.amount = amount;
        this.date = date;
    }
}
