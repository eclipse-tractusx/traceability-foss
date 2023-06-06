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

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private IrsRepository irsRepository;

    @Mock
    private AssetRepository assetRepository;


    @Test
    void synchronizeAssets_shouldSaveCombinedAssets_whenNoException() {
        // given
        List<Descriptions> parentDescriptionsList = AssetTestDataFactory.provideParentRelations();
        List<Descriptions> childDescriptionList = AssetTestDataFactory.provideChildRelations();
        String globalAssetId = "123";
        List<Asset> downwardAssets = List.of(AssetTestDataFactory.createAssetTestDataWithRelations(Collections.emptyList(), childDescriptionList));
        List<Asset> upwardAssets = List.of(AssetTestDataFactory.createAssetTestDataWithRelations(parentDescriptionsList, Collections.emptyList()));
        List<Asset> combinedAssetList = List.of(AssetTestDataFactory.createAssetTestDataWithRelations(parentDescriptionsList, childDescriptionList));

        when(irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects()))
                .thenReturn(downwardAssets);
        when(irsRepository.findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects()))
                .thenReturn(upwardAssets);


        // when
        assetService.synchronizeAssetsAsync(globalAssetId);

        // then
        verify(irsRepository).findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects());
        verify(irsRepository).findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects());
        verify(assetRepository).saveAll(any());
    }


}

