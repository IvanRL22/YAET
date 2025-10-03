package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.persistence.income.IncomeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageIncomeUseCase {

    private final IncomeDAO incomeDAO;

    public IncomeDO addNewIncome(NewIncomeRequest request) {
        return this.incomeDAO.create(request);
    }
}
