package org.eclipse.tractusx.traceability.integration.aas;

import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AASDatabaseSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AASCleanupIT extends IntegrationTestSpecification {
    @Autowired
    private AASService aasService;

    @Autowired
    private AASDatabaseSupport aasDatabaseSupport;

    @Test
    void shouldRemoveExpiredAASEntities() {
        aasDatabaseSupport.createAASEntity();
        aasDatabaseSupport.createExpiredAASEntity();
        Long elementsBeforeCleanup = aasDatabaseSupport.aasElementsInDatabase();
        assertThat(elementsBeforeCleanup).isEqualTo(2);
        aasService.aasCleanup();
        Long elementsAfterCleanup = aasDatabaseSupport.aasElementsInDatabase();
        assertThat(elementsAfterCleanup).isEqualTo(1);
    }
}
