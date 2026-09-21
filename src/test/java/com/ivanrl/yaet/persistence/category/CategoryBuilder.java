package com.ivanrl.yaet.persistence.category;

import com.ivanrl.yaet.domain.category.CategoryType;
import com.ivanrl.yaet.persistence.auth.UserPO;

// TODO This should be changed to something closer to a builder so that I can add attributes and withers
public class CategoryBuilder {

    public static CategoryPO aNormalCategory(UserPO user) {
        return new CategoryPO(user, "Some category", "A demo category", CategoryType.NORMAL);
    }
}
