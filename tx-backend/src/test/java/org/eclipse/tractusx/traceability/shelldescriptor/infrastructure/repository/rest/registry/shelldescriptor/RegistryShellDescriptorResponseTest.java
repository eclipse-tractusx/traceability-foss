package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Reference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RegistryShellDescriptorResponseTest {

    @Test
    void givenRegistryShellDescriptorResponse_whenFromCollection_thenCorrectMapping() {
        // given
        final String globalAssetId = "GLOBAL_ASSET_ID";
        final String idShort = "ID_SHORT";
        final String identification = "IDENTIFICATION";
        final String keyIdentifier = "KEY_IDENTIFIER";
        final String valueIdentifier = "VALUE_IDENTIFIER";
        IdentifierKeyValuePair identifierKeyValuePair = IdentifierKeyValuePair.builder().key(keyIdentifier).value(valueIdentifier).build();
        final Reference reference = Reference.builder().value(List.of(globalAssetId)).build();
        final AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = AssetAdministrationShellDescriptor.builder()
                .globalAssetId(reference)
                .idShort(idShort)
                .identification(identification)
                .specificAssetIds(List.of(identifierKeyValuePair))
                .build();
        final RegistryShellDescriptor expectedDescriptor = RegistryShellDescriptor.from(assetAdministrationShellDescriptor);

        // when
        final RegistryShellDescriptorResponse result = RegistryShellDescriptorResponse.fromCollection(List.of(assetAdministrationShellDescriptor));

        // then
        assertThat(result.items())
                .hasSize(1)
                .containsExactly(expectedDescriptor);
    }
}
