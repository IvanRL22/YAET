package com.ivanrl.yaet.persistence;

import com.ivanrl.yaet.AbstractArchitectureTests;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class PersistenceArchitectureTests extends AbstractArchitectureTests {

    private static final String[] ALL_PERSISTENCE_PACKAGES = {"com.ivanrl.yaet.persistence"};

    @Test
    void test_all_entities_hide_default_constructor() {
        JavaClasses webClasses = classFileImporterIgnoringTests().importPackages(ALL_PERSISTENCE_PACKAGES);

        ArchRule rule = classes().that().areAnnotatedWith(Entity.class)
                                 .should(haveProtectedEmptyConstructor());

        rule.check(webClasses);
    }

    private static ArchCondition<? super JavaClass> haveProtectedEmptyConstructor() {
        return new ArchCondition<>("have protected empty constructor for Hibernate") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
                Optional<JavaConstructor> emptyConstructorOpt = javaClass.tryGetConstructor();
                if (emptyConstructorOpt.isEmpty()) {
                    conditionEvents.add(SimpleConditionEvent.violated(javaClass,
                                                                      "entity %s is missing empty constructor".formatted(javaClass.getSimpleName())));
                    return;
                }

                if (!emptyConstructorOpt.get().getModifiers().contains(JavaModifier.PROTECTED)) {
                    conditionEvents.add(SimpleConditionEvent.violated(javaClass,
                                                                      "entity %s has non-protected empty constructor".formatted(javaClass.getSimpleName())));
                }
            }
        };
    }
}
