package com.ivanrl.yaet.persistence;

import com.ivanrl.yaet.UserData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class UserFilterAspect {

    public static final String USER_FILTER_NAME = "userFilter";

    @PersistenceContext
    EntityManager entityManager;

    private final UserData userData;

    /**
     * Matches any calls made to public methods, on DAO classes, within the persistence package
     */
    @Before("execution(public * *..persistence..*DAO.*(..))")
    private void activate() {
        if (entityManager == null) {
            log.error("Tried to activate filter without persistence context"); // Should throw exception?
            return;
        }

        var session = entityManager.unwrap(Session.class);
        if (session == null) {
            log.error("Tried to activate filter without session");  // Should throw exception?
            return;
        }

        var filter = session.getEnabledFilter(USER_FILTER_NAME);
        if (filter == null) {
            filter = session.enableFilter(USER_FILTER_NAME);
            filter.setParameter("userId", userData.getDbId());
            log.info("Activated user filter with id {}", userData.getDbId());
        } else {
            log.info("UserFilter is already active with id {}", filter.getParameterValue("userId"));
        }
    }
}
