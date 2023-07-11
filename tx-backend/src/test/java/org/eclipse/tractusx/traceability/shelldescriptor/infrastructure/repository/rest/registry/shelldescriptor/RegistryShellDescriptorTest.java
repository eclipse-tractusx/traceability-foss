package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistryShellDescriptorTest {

    @Test
    void test_ToShellDescriptor() {
        //GIVEN
        GlobalAssetId globalAssetId = new GlobalAssetId(List.of("assetId1", "assetId2"));
        List<SpecificAssetId> specificAssetIds = List.of(new SpecificAssetId("manufacturerPartId", "manufacturerPartId"));
        RegistryShellDescriptor registryShellDescriptor = new RegistryShellDescriptor(globalAssetId, "", "", specificAssetIds);

        //WHEN
        ShellDescriptor shellDescriptor = registryShellDescriptor.toShellDescriptor();

        //THEN
        assertEquals("manufacturerPartId", shellDescriptor.getManufacturerPartId());
    }
}
