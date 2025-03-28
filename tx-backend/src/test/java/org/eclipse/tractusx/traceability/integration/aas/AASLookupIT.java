package org.eclipse.tractusx.traceability.integration.aas;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.aas.application.cron.AASLookup;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AASDatabaseSupport;
import org.eclipse.tractusx.traceability.integration.common.support.DiscoveryFinderSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@Disabled("Temporarily deactivated for investigation")
public class AASLookupIT extends IntegrationTestSpecification {
    @Autowired
    private AASLookup aasLookup;

    @Autowired
    private DiscoveryFinderSupport discoveryFinderSupport;

    @Autowired
    private EdcSupport edcSupport;

    @Autowired
    private IrsApiSupport irsApiSupport;

    @Autowired
    private AASDatabaseSupport aasDatabaseSupport;

    @Test
    void shouldLookupAndStoreAAS() throws RegistryServiceException, JsonProcessingException {
        discoveryFinderSupport.discoveryFinderWillReturnEndpointAddress();
        discoveryFinderSupport.discoveryFinderWillReturnConnectorEndpointForAXS3();
        irsApiSupport.provideAcceptedPolicies();
        edcSupport.edcWillReturnCatalogRegistryAsset();
        edcSupport.edcWillCreateContractNegotiation();
        edcSupport.edcWillReturnContractNegotiationOnlyState();
        edcSupport.edcWillReturnContractNegotiationState();
        edcSupport.edcWillCreateTransferprocesses();
        edcSupport.edcWillReturnTransferprocessesOnlyState();
        edcSupport.edcWillReturnTransferprocessesState();
        edcSupport.edcWillCallCallbackController();
        edcSupport.edcWillLookupShells();
        List<String> uuids = List.of(
                "urn:uuid:9a0fdf20-f2f1-4509-9c40-d4ca9ce63bc8",
                "urn:uuid:c31072c5-997d-4d13-bb2d-1a6815d9e5df",
                "urn:uuid:6c8cb0d8-6a08-423a-b764-0eead23ef3d4",
                "urn:uuid:03c35da6-ad06-476b-85b8-89f69b7eb5cb",
                "urn:uuid:719fe1bb-9e52-4534-82b5-1dc24203e63e",
                "urn:uuid:70940a38-a66a-40d6-9884-534fac7a56c0",
                "urn:uuid:29ee8b16-19d5-40ee-9c5f-bc25cf0ee527",
                "urn:uuid:b6f2d4bb-ed0d-4fcf-a839-856235c0cf0f",
                "urn:uuid:91adaa05-f893-41b9-b5a0-d278eeba105e",
                "urn:uuid:4b740034-76f1-46e0-b167-3546b175fe0a",
                "urn:uuid:29a2375d-b545-4648-9706-c38732fe2222"
        );
        List<AAS> existingAASIdsBeforeInsert = aasDatabaseSupport.findExistingAASIds(uuids);
        assertThat(existingAASIdsBeforeInsert.size()).isEqualTo(0);

        aasLookup.aasLookupByType(TwinType.PART_TYPE);

        List<AAS> existingAASIdsAfter = aasDatabaseSupport.findExistingAASIds(uuids);
        assertThat(existingAASIdsAfter.size()).isEqualTo(11);
    }
}
