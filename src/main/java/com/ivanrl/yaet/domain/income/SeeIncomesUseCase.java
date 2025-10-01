package com.ivanrl.yaet.domain.income;

import com.ivanrl.yaet.domain.income.persistence.IncomePO;
import com.ivanrl.yaet.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeeIncomesUseCase {

    private final IncomeRepository incomeRepository;

    public Page<IncomePO> getIncomes() {
        return this.incomeRepository.findByOrderByDateDesc(Pageable.ofSize(10));
    }
}
