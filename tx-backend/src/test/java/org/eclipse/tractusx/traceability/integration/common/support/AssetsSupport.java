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
package org.eclipse.tractusx.traceability.integration.common.support;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository.JpaInvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AssetsSupport {

    @Autowired
    AssetRepositoryProvider assetRepositoryProvider;

    @Autowired
    JpaInvestigationRepository jpaInvestigationRepository;

    public String emptyText() {
        return "--";
    }

    public void defaultAssetsStored() {
        assetRepositoryProvider.assetAsBuiltRepository().saveAll(assetRepositoryProvider.assetsConverter().readAndConvertAssetsForTests());
    }

    public void defaultAssetsAsPlannedStored() {
        assetRepositoryProvider.assetAsPlannedRepository().saveAll(assetRepositoryProvider.assetsConverter().readAndConvertAssetsAsPlannedForTests());
    }

    public void defaultAssetsStoredWithOnGoingInvestigation(QualityNotificationStatusBaseEntity investigationStatus, boolean inInvestigation) {
        List<AssetAsBuiltEntity> assetEntities = assetRepositoryProvider.assetsConverter().readAndConvertAssetsForTests().stream().map(asset -> {
            AssetAsBuiltEntity assetEntity = AssetAsBuiltEntity.from(asset);
            assetEntity.setInInvestigation(inInvestigation);
            return assetEntity;
        }).toList();

        assetEntities.stream().map(it ->
                InvestigationEntity.builder()
                        .assets(List.of(it))
                        .bpn(it.getManufacturerId())
                        .status(investigationStatus)
                        .side(QualityNotificationSideBaseEntity.SENDER)
                        .description("some long description")
                        .createdDate(Instant.now())
                        .build()
        ).toList().forEach(it -> jpaInvestigationRepository.save(it));
    }

    public void assertAssetAsBuiltSize(int size) {
        long assetCount = assetRepositoryProvider.assetAsBuiltRepository().countAssets();
        log.info("AsBuiltRepository asset count: {}, expected: {}", assetCount, size);
        assert assetCount == size;
    }

    public void assertAssetAsPlannedSize(int size) {
        long assetCount = assetRepositoryProvider.assetAsPlannedRepository().countAssets();
        log.info("AsPlannedRepository asset count: {}", assetCount);
        assert assetCount == size;
    }

    public void assertHasRequiredIdentifiers() {
        assetRepositoryProvider.assetAsBuiltRepository().getAssets().forEach(asset -> {
            log.info("!asset.getManufacturerId().equals(\"--\"): {}", !asset.getManufacturerId().equals("--"));
            log.info("!asset.getSemanticModelId().equals(\"--\"): {}", !asset.getSemanticModelId().equals("--"));
            log.info("!Objects.isNull(asset.getIdShort()): {}", !Objects.isNull(asset.getIdShort()));

            assert !asset.getManufacturerId().equals("--");
            assert !asset.getSemanticModelId().equals("--");
            assert !Objects.isNull(asset.getIdShort());
        });
    }

    public void assertAsPlannedHasRequiredIdentifiers() {
        assetRepositoryProvider.assetAsPlannedRepository().getAssets().forEach(asset -> {
            assert Objects.nonNull(asset.getManufacturerId()) && !asset.getManufacturerId().equals("--");
            assert Objects.nonNull(asset.getSemanticModelId()) && !asset.getSemanticModelId().equals("--");
            assert !Objects.isNull(asset.getIdShort());

            log.info("!asset.getManufacturerId().equals(\"--\"): {}", !asset.getManufacturerId().equals("--"));
            log.info("!asset.getSemanticModelId().equals(\"--\"): {}", !asset.getSemanticModelId().equals("--"));
            log.info("!Objects.isNull(asset.getIdShort()): {}", !Objects.isNull(asset.getIdShort()));
        });
    }

    public void assertHasChildCount(String assetId, int count) {
        List<Descriptions> childRelations = assetRepositoryProvider.assetAsBuiltRepository().getAssetById(assetId).getChildRelations();
        log.info("childCount: {}", childRelations.size());
        assert childRelations.size() == count;
    }

    public void assertHasAsPlannedChildCount(String assetId, int count) {
        List<Descriptions> childRelations = assetRepositoryProvider.assetAsPlannedRepository().getAssetById(assetId).getChildRelations();
        log.info("childCount: {}", childRelations.size());
        assert childRelations.size() == count;
    }

    public void assertNoAssetsStored() {
        assertAssetAsBuiltSize(0);
    }
}
