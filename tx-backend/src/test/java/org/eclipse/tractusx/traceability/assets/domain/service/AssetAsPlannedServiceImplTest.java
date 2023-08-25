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
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetAsPlannedServiceImplTest {

    @InjectMocks
    private AssetAsPlannedServiceImpl assetService;

    @Mock
    private IrsRepository irsRepository;

    @Mock
    private AssetAsPlannedRepository assetRepository;

    @Test
    void synchronizeAssets_shouldSaveCombinedAssets_whenNoException() {
        // given
        List<Descriptions> parentDescriptionsList = AssetTestDataFactory.provideParentRelations();
        List<Descriptions> childDescriptionList = AssetTestDataFactory.provideChildRelations();
        String globalAssetId = "123";
        List<AssetBase> downwardAssets = List.of(AssetTestDataFactory.createAssetTestDataWithRelations(Collections.emptyList(), childDescriptionList));
        List<AssetBase> upwardAssets = List.of(AssetTestDataFactory.createAssetTestDataWithRelations(parentDescriptionsList, Collections.emptyList()));

        when(irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspectsForAssetsAsPlanned(), BomLifecycle.AS_PLANNED))
                .thenReturn(downwardAssets);

        // when
        assetService.synchronizeAssetsAsync(globalAssetId);

        // then
        verify(irsRepository).findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspectsForAssetsAsPlanned(), BomLifecycle.AS_PLANNED);
        verify(assetRepository, times(1)).saveAll(any());
    }


}

