package com.ivanrl.yaet.persistence.income;


import com.ivanrl.yaet.domain.income.IncomeDO;
import com.ivanrl.yaet.persistence.auth.UserPO;
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

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserPO user;

    @Column(name = "payer", length = 50)
    private String payer;

    @Column(name = "amount", precision = 6, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    public IncomePO(UserPO user, String payer, BigDecimal amount, LocalDate date) {
        this.user = user;
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
