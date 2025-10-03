package com.ivanrl.yaet.persistence.income;

import com.ivanrl.yaet.domain.income.IncomeDO;
import com.ivanrl.yaet.domain.income.NewIncomeRequest;
import com.ivanrl.yaet.domain.income.persistence.IncomePO;
import com.ivanrl.yaet.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IncomeDAO {

    private final IncomeRepository repository;

    public IncomeDO create(NewIncomeRequest request) {
        IncomePO newPO = new IncomePO(request.payer(),
                                       request.amount(),
                                       request.date());

        this.repository.save(newPO);

        return newPO.toDomainModel();
    }

    public Page<IncomeDO> getLastExpenses(Pageable pageable) {
        return this.repository.findByOrderByDateDesc(pageable).map(IncomePO::toDomainModel);
    }
}
