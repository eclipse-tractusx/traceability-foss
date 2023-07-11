package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

class RegistryShellDescriptorTest {

    @Test
    void test_ToShellDescriptor() {
        //GIVEN
        GlobalAssetId globalAssetId = new GlobalAssetId(List.of("assetId1", "assetId2"));
        RegistryShellDescriptor registryShellDescriptor = new RegistryShellDescriptor(globalAssetId, "", "", Collections.emptyList());

        //WHEN
        ShellDescriptor shellDescriptor = registryShellDescriptor.toShellDescriptor();

        //THEN
        assertNull(shellDescriptor.getManufacturerId());
    }
}
