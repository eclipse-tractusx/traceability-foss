package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShellDescriptorTest {

    @Test
    void testFromGlobalAssetId() {
        String globalAssetId = "ABC123";
        ShellDescriptor descriptor = ShellDescriptor.fromGlobalAssetId(globalAssetId);

        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getGlobalAssetId()).isEqualTo(globalAssetId);
    }

    @Test
    void testFromGlobalAssetIds() {
        List<String> globalAssetIds = Arrays.asList("ABC123", "DEF456", "GHI789");
        List<ShellDescriptor> descriptors = ShellDescriptor.fromGlobalAssetIds(globalAssetIds);

        assertThat(descriptors).isNotNull().hasSize(globalAssetIds.size());

        for (int i = 0; i < globalAssetIds.size(); i++) {
            assertThat(descriptors.get(i)).isNotNull();
            assertThat(descriptors.get(i).getGlobalAssetId()).isEqualTo(globalAssetIds.get(i));
        }
    }
}
