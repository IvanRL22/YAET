package com.ivanrl.yaet;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Provides base logic for architecture tests, both for the whole app and for the layers
 */
public abstract class AbstractArchitectureTests {

    protected static final String JAKARTA_PERSISTENCE_ENTITY_ANNOTATION_NAME = "jakarta.persistence.Entity";

    private final ImportOption ignoreTests = location -> !location.contains("/test/");

    protected ClassFileImporter classFileImporterIgnoringTests() {
        return new ClassFileImporter().withImportOption(ignoreTests);
    }
}
