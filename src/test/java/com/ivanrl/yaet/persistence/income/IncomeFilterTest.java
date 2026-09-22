package com.ivanrl.yaet.persistence.income;

import com.ivanrl.yaet.UserData;
import com.ivanrl.yaet.persistence.auth.UserPO;
import com.ivanrl.yaet.persistence.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class IncomeFilterTest {

    @Autowired
    private IncomeRepository repository;

    @Autowired
    private IncomeDAO dao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserData userData;

    /**
     * When there are several incomes in the DB for different users, only the ones for the current user should be returned
     */
    @Test
    void userShouldOnlySeeTheirOwnBudgetCategories() {
        // Create an income for the current user
        var currentUser = new UserPO("Test User", "test@yaet.com");
        userRepository.save(currentUser);

        var currentIncome = new IncomePO(currentUser,
                                         "My employee",
                                         new BigDecimal("2134.56"),
                                         LocalDate.now());
        repository.save(currentIncome);

        // Create a parallel income for a different user
        var otherUser = new UserPO("User 1", "mail1@mail.com");
        userRepository.save(otherUser);

        var otherIncome = new IncomePO(otherUser,
                                       "Some other employee",
                                       new BigDecimal("1234.56"),
                                       LocalDate.now());
        repository.save(otherIncome);

        // Set user data ID for the current user
        userData.setDbId(currentUser.getId());

        // Test
        var allByResult = dao.getLastExpenses(Pageable.unpaged());

        var expectedResult = currentIncome.toDomainModel();
        assertThat(allByResult.stream().toList())
                .withFailMessage("List should only the income of the current user")
                .containsExactly(expectedResult);

    }
}
