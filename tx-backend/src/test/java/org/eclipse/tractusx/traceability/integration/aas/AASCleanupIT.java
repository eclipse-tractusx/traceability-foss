package org.eclipse.tractusx.traceability.integration.aas;

import org.eclipse.tractusx.traceability.aas.application.cron.AASCleanup;
import org.eclipse.tractusx.traceability.aas.application.cron.AASLookup;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AASDatabaseSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AASCleanupIT extends IntegrationTestSpecification {
    @Autowired
    private AASLookup aasLookup;

    @Autowired
    private AASCleanup aasCleanup;

    @Autowired
    private AASDatabaseSupport aasDatabaseSupport;

    @Test
    void shouldRemoveExpiredAASEntities() {
        aasDatabaseSupport.createAASEntity();
        aasDatabaseSupport.createExpiredAASEntity();
        Long elementsBeforeCleanup = aasDatabaseSupport.aasElementsInDatabase();
        assertThat(elementsBeforeCleanup).isEqualTo(2);
        aasCleanup.aasCleanup();
        Long elementsAfterCleanup = aasDatabaseSupport.aasElementsInDatabase();
        assertThat(elementsAfterCleanup).isEqualTo(1);
    }
}
