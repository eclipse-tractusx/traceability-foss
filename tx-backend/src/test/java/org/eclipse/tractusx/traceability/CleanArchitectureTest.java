/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CleanArchitectureTest {

    private static final ImportOption ignoreTests = location -> {
        return !(location.contains("/test/") || location.contains("/test-classes/")); // ignore any URI to sources that contains '/test/'
    };

    private JavaClasses importedClasses;

    @BeforeEach
    void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOptions(List.of(ignoreTests))
                .importPackages("org.eclipse.tractusx.traceability");
    }

    @Test
    void givenDependencyRule_notImportInfrastructureInApplication() {
        ArchRule rule = noClasses().that()
                .resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    void givenDependencyRule_notImportApplicationInInfrastructure() {
        ArchRule rule = noClasses().that()
                .resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..application..");

        rule.check(importedClasses);
    }

    @Test
    @Disabled("Not all ready to turn on this test")
    void givenDependencyRule_notImportApplicationInDomain() {
        ArchRule rule = noClasses().that()
                .resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..");

        rule.check(importedClasses);
    }

    @Test
    @Disabled("Not all ready to turn on this test")
    void givenDependencyRule_notImportInfrastructureInDomain() {
        ArchRule rule = noClasses().that()
                .resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    @Disabled("Not all ready to turn on this test")
    void validate_Architecture() {
        Architectures.LayeredArchitecture arch = Architectures.layeredArchitecture()
                .consideringAllDependencies()
                // Define layers
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .layer("Common").definedBy("..common..") // maybe define common.domain ?
                // Add constraints
                .whereLayer("Application").mayNotBeAccessedByAnyLayer()
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Infrastructure", "Application", "Common")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
        arch.check(importedClasses);
    }
}
