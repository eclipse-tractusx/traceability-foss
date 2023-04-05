package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.metrics.RegistryLookupMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegistryServiceTest {

    @InjectMocks
    private RegistryService registryService;

    @Mock
    private RegistryApiClient registryApiClient;

    @Mock
    private RegistryLookupMeterRegistry registryLookupMeterRegistry;

    @Mock
    private RegistryShellDescriptorResponse registryShellDescriptorResponse;

    @Mock
    private RegistryShellDescriptor registryShellDescriptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        String bpn = "test-bpn";
        String manufacturerIdKey = "test-manufacturer-id-key";

        registryService = new RegistryService(objectMapper, registryApiClient, bpn, manufacturerIdKey, registryLookupMeterRegistry, Clock.systemUTC());

    }

    @Test
    void testFindAssets() {
        // Given
        when(registryShellDescriptor.globalAssetId()).thenReturn(new GlobalAssetId(List.of("testGlobalAssetId")));

        List<RegistryShellDescriptor> items = new ArrayList<>();
        items.add(registryShellDescriptor);
        when(registryShellDescriptorResponse.items()).thenReturn(items);

        List<String> assetIds = Collections.singletonList("testAssetId");
        when(registryApiClient.getShells(Collections.singletonMap("assetIds", Collections.singletonList("testManufacturerIdKey:testBpn"))))
                .thenReturn(assetIds);
        when(registryApiClient.fetchShellDescriptors(any())).thenReturn(registryShellDescriptorResponse);

        // When
        List<ShellDescriptor> shellDescriptors = registryService.findAssets();

        // Then
        assertEquals(1, shellDescriptors.size());
    }
}
