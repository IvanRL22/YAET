package com.ivanrl.yaet.persistence.income;


import com.ivanrl.yaet.domain.income.IncomeDO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.ivanrl.yaet.persistence.UserFilterAspect.USER_FILTER_NAME;

@Filter(name = USER_FILTER_NAME)
@Entity(name = "incomes")
@Table(name = "incomes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class IncomePO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private int userId;

    @Column(name = "payer", length = 50)
    private String payer;

    @Column(name = "amount", precision = 8, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public IncomePO(int userId, String payer, BigDecimal amount, LocalDate date) {
        this.userId = userId;
        this.payer = payer;
        this.amount = amount;
        this.date = date;
    }

    public IncomeDO toDomainModel() {
        return new IncomeDO(this.getId(),
                            this.getPayer(),
                            this.getDate(),
                            this.getAmount());
    }
}
