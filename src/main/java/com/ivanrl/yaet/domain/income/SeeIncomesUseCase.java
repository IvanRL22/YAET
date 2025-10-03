package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.persistence.income.IncomeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeeIncomesUseCase {

    private final IncomeDAO incomeDAO;

    public Page<IncomeDO> getIncomes() {
        return this.incomeDAO.getLastExpenses(Pageable.ofSize(10));
    }
}
