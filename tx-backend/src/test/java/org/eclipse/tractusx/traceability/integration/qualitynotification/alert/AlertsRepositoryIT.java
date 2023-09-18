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

package org.eclipse.tractusx.traceability.integration.qualitynotification.alert;

import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlertsRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    JpaAlertRepository jpaAlertRepository;

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository assetAsBuiltRepository;

    @Autowired
    AlertRepository repository;

    @Test
    void givenAlertsWithAssets_whenCountDistinctAffectedSupplierPartsWithAlertReceivedStatus_thenReturnProperCount() {
        // given
        assetsSupport.defaultAssetsStored();
        List<AssetAsBuiltEntity> assets = assetAsBuiltRepository.findAll();
        List<AssetAsBuiltEntity> ownAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.OWN))
                .toList();
        List<AssetAsBuiltEntity> supplierAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.SUPPLIER))
                .toList();
        alertsSupport.storeAlertWithStatusAndAssets(supplierAssets);
        alertsSupport.storeAlertWithStatusAndAssets(supplierAssets);
        alertsSupport.defaultSentAlertStoredForAssets(ownAssets);
        assertThat(jpaAlertRepository.findAll()).hasSize(3);

        // when
        Long result = repository.countPartsByStatusAndOwnership(List.of(QualityNotificationStatus.RECEIVED), Owner.SUPPLIER);

        // then
        assertThat(result).isEqualTo(12);
    }

    @Test
    void givenAlertsWithAssets_whenCountDistinctAffectedOwnPartsWithAlertSentStatus_thenReturnProperCount() {
        // given
        assetsSupport.defaultAssetsStored();
        List<AssetAsBuiltEntity> assets = assetAsBuiltRepository.findAll();
        List<AssetAsBuiltEntity> ownAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.OWN))
                .toList();
        List<AssetAsBuiltEntity> supplierAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.SUPPLIER))
                .toList();
        alertsSupport.storeAlertWithStatusAndAssets(supplierAssets);
        alertsSupport.defaultSentAlertStoredForAssets(ownAssets);
        alertsSupport.defaultSentAlertStoredForAssets(ownAssets);
        assertThat(jpaAlertRepository.findAll()).hasSize(3);

        // when
        Long result = repository.countPartsByStatusAndOwnership(List.of(QualityNotificationStatus.SENT), Owner.OWN);

        // then
        assertThat(result).isEqualTo(1);
    }
}
