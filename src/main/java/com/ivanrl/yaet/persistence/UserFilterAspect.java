package com.ivanrl.yaet.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class UserFilterAspect {

    @PersistenceContext
    EntityManager entityManager;

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

        if (session.getEnabledFilter("userFilter") == null) {
            session.enableFilter("userFilter").setParameter("userId", 1); // TODO Get id from user data
            log.info("Activated user filter with id {}", 1);
        } else {
            log.info("UserFilter is already active");
        }
    }
}
