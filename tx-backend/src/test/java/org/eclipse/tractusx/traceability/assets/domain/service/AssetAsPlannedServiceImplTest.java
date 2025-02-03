/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetAsPlannedServiceImplTest {

    @InjectMocks
    private AssetAsPlannedServiceImpl assetService; // Injects mocks into the service

    @Mock
    private OrderRepository orderRepository; // Mock for OrderRepository

    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository; // Mock the concrete repository

    @Test
    void synchronizeAssets_shouldSaveCombinedAssets_whenNoException() {
        // Given
        String globalAssetId = "123";
        List<String> globalAssetIds = List.of(globalAssetId);

        AssetBase mockAssetBase = AssetBase.builder()
                .id(globalAssetId)
                .manufacturerId("456")
                .build();

        List<AssetBase> mockAssets = List.of(mockAssetBase);

        // Mock the concrete repository's behavior
        when(assetAsPlannedRepository.getAssetsById(globalAssetIds)).thenReturn(mockAssets);

        // When
        assetService.syncAssetsAsyncUsingIRSOrderAPI(globalAssetIds);

        // Then
        verify(orderRepository).createOrderToResolveAssets(
                mockAssets,
                Direction.DOWNWARD,
                Aspect.downwardAspectsForAssetsAsPlanned(), // Match AS_PLANNED aspects
                BomLifecycle.AS_PLANNED // Match AS_PLANNED lifecycle
        );
    }
}


