package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Reference;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void givenAssetAdministrationShellDescriptor_whenFrom_thenConvertProperly() {
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

        // when
        final RegistryShellDescriptor result = RegistryShellDescriptor.from(assetAdministrationShellDescriptor);

        // then
        assertThat(result.globalAssetId().value())
                .containsExactly(globalAssetId);
        assertThat(result.identification()).isEqualTo(identification);
        assertThat(result.idShort()).isEqualTo(idShort);
        assertThat(result.specificAssetIds())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("key", keyIdentifier)
                .hasFieldOrPropertyWithValue("value", valueIdentifier);
    }
}
