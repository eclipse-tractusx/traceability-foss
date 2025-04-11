/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import org.eclipse.tractusx.traceability.assets.application.importpoc.PublishServiceAsync;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PublishAssetException;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.DecentralRegistryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublishServiceImplTest {
    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository;
    @Mock
    private AssetAsBuiltRepository assetAsBuiltRepository;
    @Mock
    private EdcAssetCreationService edcAssetCreationService;
    @Mock
    private DtrService dtrService;
    @Mock
    private DecentralRegistryServiceImpl decentralRegistryService;
    @Mock
    private PublishServiceAsync publishServiceAsync;
    @InjectMocks
    private PublishServiceImpl publishService;
    private final OrderConfiguration orderConfig = OrderConfiguration.builder().build();

    @Test
    void testPublishAssets_shouldTriggerCoreServicesPublishing() {
        OrderConfiguration orderConfiguration = mock(OrderConfiguration.class);

        AssetBase asset1 = mock(AssetBase.class);
        AssetBase asset2 = mock(AssetBase.class);

        when(asset1.getId()).thenReturn("asset-1");
        when(asset2.getId()).thenReturn("asset-2");

        when(assetAsBuiltRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION)).thenReturn(List.of(asset1));
        when(assetAsPlannedRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION)).thenReturn(List.of(asset2));

        publishService.publishAssets(orderConfiguration);

        verify(assetAsBuiltRepository).findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        verify(assetAsPlannedRepository).findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        verify(publishServiceAsync).publishAssetsToCoreServices(any(), anyBoolean(), any());
    }


    @Test
    void publishAssets_shouldFetchAssetsAndTriggerAsyncPublishing() {
        AssetBase asset1 = mock(AssetBase.class);
        AssetBase asset2 = mock(AssetBase.class);

        when(assetAsBuiltRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION)).thenReturn(List.of(asset1));
        when(assetAsPlannedRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION)).thenReturn(List.of(asset2));

        publishService.publishAssets(orderConfig);

        verify(publishServiceAsync).publishAssetsToCoreServices(List.of(asset2, asset1), true, orderConfig);
    }

    @Test
    void publishAssets_withIds_shouldUpdateAndTriggerAsyncPublishing() {
        String policyId = "test-policy";
        String assetId = "asset1";

        AssetBase asset = AssetBase.builder().id(assetId).importState(ImportState.TRANSIENT).build();

        when(assetAsBuiltRepository.existsById(assetId)).thenReturn(true);
        when(assetAsBuiltRepository.getAssetsById(List.of(assetId))).thenReturn(List.of(asset));
        when(assetAsBuiltRepository.saveAll(any())).thenReturn(List.of(asset));

        publishService.publishAssets(policyId, List.of(assetId), false, orderConfig);

        verify(publishServiceAsync).publishAssetsToCoreServices(anyList(), eq(false), eq(orderConfig));
    }

    @Test
    void publishAssets_shouldThrowIfAssetNotFound() {
        String assetId = "nonexistent";

        when(assetAsBuiltRepository.existsById(assetId)).thenReturn(false);
        when(assetAsPlannedRepository.existsById(assetId)).thenReturn(false);

        assertThrows(PublishAssetException.class, () ->
                publishService.publishAssets("policy", List.of(assetId), true, orderConfig));
    }

    @Test
    void updateAssetWithStatusAndPolicy_shouldThrowIfInvalidState() {
        assertThrows(PublishAssetException.class, () ->
                publishService.publishAssets("policy", List.of("123"), true, orderConfig));
    }


}
