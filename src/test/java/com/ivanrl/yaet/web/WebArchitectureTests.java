package com.ivanrl.yaet.web;

import com.ivanrl.yaet.AbstractArchitectureTests;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class WebArchitectureTests extends AbstractArchitectureTests {

    private static final String ALL_WEB_PACKAGES = "com.ivanrl.yaet.web";

    // Currently web relies on the domain model objects returned by the domain
    // Eventually it would be good if it had its own transfer objects
    @Test
    void check_that_web_controllers_do_not_depend_persistence() {
        JavaClasses webClasses = classFileImporterIgnoringTests().importPackages(ALL_WEB_PACKAGES);

        ArchRule rule = noClasses().that().areAnnotatedWith(Controller.class)
                                   .should().dependOnClassesThat().areAnnotatedWith(Entity.class);

        rule.check(webClasses);
    }

}
