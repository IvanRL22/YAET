package com.ivanrl.yaet.persistence.budget;

import com.ivanrl.yaet.UserData;
import com.ivanrl.yaet.persistence.auth.UserPO;
import com.ivanrl.yaet.persistence.auth.UserRepository;
import com.ivanrl.yaet.persistence.category.CategoryBuilder;
import com.ivanrl.yaet.persistence.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BudgetCategoryFilterTest {

    @Autowired
    private BudgetCategoryRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetCategoryDAO budgetCategoryDAO;

    @Autowired
    private UserData userData;

    /**
     * When there are several BudgetCategoryPO persisted for different users, only the ones for the current user should be returned
     */
    @Test
    void userShouldOnlySeeTheirOwnBudgetCategories() {
        // Create a budget category for the current user
        var currentUser = new UserPO("Test User", "test@yaet.com");
        userRepository.save(currentUser);

        var currentCategory = CategoryBuilder.aNormalCategory();
        currentCategory.setName("The good category");
        categoryRepository.save(currentCategory);

        var currentBudgetCategory = new BudgetCategoryPO(currentUser,
                                                         currentCategory,
                                                         YearMonth.now(),
                                                         BigDecimal.ZERO,
                                                         BigDecimal.ZERO);
        repository.save(currentBudgetCategory);

        // Create a parallel budget category for a different user
        var otherUser = new UserPO("User 1", "mail1@mail.com");
        userRepository.save(otherUser);

        var otherCategory = CategoryBuilder.aNormalCategory();
        categoryRepository.save(otherCategory);

        var otherBudgetCategory = new BudgetCategoryPO(otherUser,
                                                       otherCategory,
                                                       YearMonth.now(),
                                                       BigDecimal.ZERO,
                                                       BigDecimal.ZERO);
        repository.save(otherBudgetCategory);

        // Set user data ID for the current user
        userData.setDbId(currentUser.getId());

        // Test
        var allByResult = budgetCategoryDAO.findAllBy(YearMonth.now());

        var expectedResult = currentBudgetCategory.toSimpleDomainModel();
        assertThat(allByResult)
                .withFailMessage("Expected the list to contain only the category:\n\t%s\nbut instead contains:\n\t%s",
                                 expectedResult,
                                 allByResult.stream().map(Record::toString).collect(Collectors.joining(",")))
                .containsExactly(expectedResult);

    }
}
