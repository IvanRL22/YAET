package com.ivanrl.yaet;

import com.ivanrl.yaet.domain.DomainModel;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class YaetArchitectureTests extends AbstractArchitectureTests {

    private final JavaClasses ALL_CLASSES = classFileImporterIgnoringTests()
            .importPackages("com.ivanrl.yaet..");


    @Test
    void test_architecture_layers() {
        var architecture = layeredArchitecture().consideringOnlyDependenciesInAnyPackage( "com.ivanrl.yaet..")
                                                .ensureAllClassesAreContainedInArchitectureIgnoring("com.ivanrl.yaet") // Ignore global classes
                                                .layer("Web").definedBy("..web..")
                                                .layer("Domain").definedBy("..domain..")
                                                .layer("Persistence").definedBy("..persistence..");

        // Ideally in the future domain would not depend on anybody
        // All the layers should depend on Domain
        architecture.whereLayer("Web").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Web").mayOnlyAccessLayers("Domain")
                    .whereLayer("Domain").mayOnlyAccessLayers("Persistence")
                    .whereLayer("Persistence").mayNotAccessAnyLayer();

        architecture.check(ALL_CLASSES);
    }

    @Test
    void test_that_persistence_entities_only_reside_on_persistence_layer() {

        ArchRule rule = classes().that().areAnnotatedWith(JAKARTA_PERSISTENCE_ENTITY_ANNOTATION_NAME)
                                 .should().resideInAPackage("com.ivanrl.yaet.persistence..");

        rule.because("entities should be declared only on persistence layer").check(ALL_CLASSES);
    }

    @Test
    void test_that_domain_model_only_resides_on_domain_layer() {

        ArchRule rule = classes().that().areAnnotatedWith(DomainModel.class)
                                 .should().resideInAPackage("com.ivanrl.yaet.domain..");

        rule.because("domain model should be declared only on domain layer").check(ALL_CLASSES);
    }

}
