package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.domain.income.persistence.IncomePO;
import com.ivanrl.yaet.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageIncomeUseCase {

    private final IncomeRepository incomeRepository;

    public IncomeDO addNewIncome(NewIncomeRequest request) {
        IncomePO newPO = this.incomeRepository.save(new IncomePO(request.payer(),
                                                                 request.amount(),
                                                                 request.date()));

        return IncomeDO.from(newPO);
    }
}
