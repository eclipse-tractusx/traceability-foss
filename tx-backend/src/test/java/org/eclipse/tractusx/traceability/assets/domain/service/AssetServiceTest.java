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
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Direction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    void combineAssetsAndMergeParentDescriptionIntoDownwardAssets() {

        // given
        List<Descriptions> parentDescriptionsList = provideParentDescriptions();
        List<Descriptions> childDescriptionList = provideChildDescriptions();
        Asset asset = provideTestAsset(childDescriptionList, Collections.emptyList());
        Asset asset2 = provideTestAsset(Collections.emptyList(), parentDescriptionsList);

        // when
        List<Asset> assets = assetService.combineAssetsAndMergeParentDescriptionIntoDownwardAssets(List.of(asset), List.of(asset2));

        // then
        assertThat(assets).hasSize(1);
        assertThat(assets.get(0).getChildDescriptions()).hasSize(2);
        assertThat(assets.get(0).getParentDescriptions()).hasSize(2);
    }

    @Test
    void synchronizeAssets_shouldSaveCombinedAssets_whenNoException() {
        // given
        List<Descriptions> parentDescriptionsList = provideParentDescriptions();
        List<Descriptions> childDescriptionList = provideChildDescriptions();
        String globalAssetId = "123";
        List<Asset> downwardAssets = List.of(provideTestAsset(childDescriptionList, Collections.emptyList()));
        List<Asset> upwardAssets = List.of(provideTestAsset(Collections.emptyList(), parentDescriptionsList));
        List<Asset> combinedAssetList = List.of(provideTestAsset(childDescriptionList, parentDescriptionsList));

        when(irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects()))
                .thenReturn(downwardAssets);
        when(irsRepository.findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects()))
                .thenReturn(upwardAssets);
        when(assetRepository.saveAll(combinedAssetList))
                .thenReturn(combinedAssetList);

        // when
        assetService.synchronizeAssetsAsync(globalAssetId);

        // then
        verify(irsRepository).findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects());
        verify(irsRepository).findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects());
        verify(assetRepository).saveAll(any());
        verifyNoMoreInteractions(irsRepository, assetRepository);
    }


    private List<Descriptions> provideChildDescriptions() {
        List<Descriptions> childDescriptionList = new ArrayList<>();
        childDescriptionList.add(new Descriptions("childId", "idshort"));
        childDescriptionList.add(new Descriptions("childId2", "idshort"));
        return childDescriptionList;
    }


    private List<Descriptions> provideParentDescriptions() {
        List<Descriptions> parentDescriptionsList = new ArrayList<>();
        parentDescriptionsList.add(new Descriptions("parentId", "idshort"));
        parentDescriptionsList.add(new Descriptions("parentId2", "idshort"));
        return parentDescriptionsList;
    }

    private Asset provideTestAsset(List<Descriptions> childDescriptions, List<Descriptions> parentDescriptions) {
        String id = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd";
        String idShort = "--";
        String nameAtManufacturer = "1";
        String manufacturerPartId = "33740332-54";
        String partInstanceId = "NO-297452866581906730261974";
        String manufacturerId = "BPNL00000003CSGV";
        String batchId = "--";
        String manufacturerName = "Tier C";
        String nameAtCustomer = "Door front-right";
        String customerPartId = "33740332-54";
        Instant manufacturingDate = Instant.parse("2022-02-04T13:48:54Z");
        String manufacturingCountry = "DEU";
        Owner owner = Owner.CUSTOMER;
        QualityType qualityType = QualityType.OK;
        String van = "--";
        return new Asset(id, idShort, nameAtManufacturer, manufacturerPartId, partInstanceId, manufacturerId, batchId, manufacturerName, nameAtCustomer, customerPartId, manufacturingDate, manufacturingCountry, owner, childDescriptions, parentDescriptions, false, qualityType, van);

    }

}

