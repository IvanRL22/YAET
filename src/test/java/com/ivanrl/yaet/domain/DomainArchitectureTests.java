package com.ivanrl.yaet.domain;

import com.ivanrl.yaet.AbstractArchitectureTests;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainArchitectureTests extends AbstractArchitectureTests {

    private static final String[] ALL_DOMAIN_PACKAGES = {"com.ivanrl.yaet.domain"}; // Ignoring common classes on base package
    private static final String[] PACKAGES_ALLOWED = {
            "java..", // Since we may need String, List and other java classes
            "com.ivanrl.yaet.domain..",
            "lombok.."
    };

    @Test
    void check_that_domain_does_not_depend_on_entities() {
        JavaClasses webClasses = classFileImporterIgnoringTests().importPackages(ALL_DOMAIN_PACKAGES);

        ArchRule rule = noClasses().should().dependOnClassesThat().areAnnotatedWith(Entity.class);

        rule.because("domain should not care about persistence details").check(webClasses);
    }

    @Test
    void check_that_domain_objects_only_depend_on_other_domain_objects() {
        JavaClasses domainClasses = classFileImporterIgnoringTests().importPackages(ALL_DOMAIN_PACKAGES);

        ArchRule rule = noClasses().that().areAnnotatedWith(DomainModel.class)
                                   .should().dependOnClassesThat().resideOutsideOfPackages(PACKAGES_ALLOWED);

        rule.because("domain model should not depend on external classes").check(domainClasses);
    }

    @Test
    void test_that_domain_does_not_depend_on_web() {
        JavaClasses domainClasses = classFileImporterIgnoringTests().importPackages(ALL_DOMAIN_PACKAGES);

        ArchRule rule = noClasses().should().dependOnClassesThat().resideInAnyPackage("com.ivanrl.yaet.web");

        rule.because("domain should not depend on web").check(domainClasses);
    }
}
