package com.ivanrl.yaet;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Provides base logic for architecture tests, both for the whole app and for the layers
 */
public abstract class AbstractArchitectureTests {

    private final ImportOption ignoreTests = location -> !location.contains("/test/");

    protected ClassFileImporter classFileImporterIgnoringTests() {
        return new ClassFileImporter().withImportOption(ignoreTests);
    }
}
