package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.UserData;
import com.ivanrl.yaet.persistence.auth.UserPO;
import com.ivanrl.yaet.persistence.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CategoryFilterTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private UserData userData;

    @Autowired
    private CategoryDAO categoryDAO;


    @Test
    void userShouldOnlySeeTheirOwnCategories() {
        // Create user and category for it
        var currentUser = new UserPO("Test user", "mail@yaet.com");
        userRepository.save(currentUser);

        var currentCategory = CategoryBuilder.aNormalCategory(currentUser);
        repository.save(currentCategory);

        // Create another user with a different category
        var otherUser = new UserPO("Other user", "other@mail.com");
        userRepository.save(otherUser);

        var otherCategory = CategoryBuilder.aNormalCategory(otherUser);
        otherCategory.setName("A different category");
        repository.save(otherCategory);

        // Set user id
        userData.setDbId(currentUser.getId());

        // Test
        var result = categoryDAO.getAllSimple();

        var expectedResult = currentCategory.toSimpleDomainModel();
        assertThat(result)
                .withFailMessage("Expected result to only contain:\n\t%s\nbut instead contains:\n\t%s",
                                 expectedResult,
                                 result.stream().map(Record::toString).collect(Collectors.joining(",")))
                .containsExactly(expectedResult);
    }
}

