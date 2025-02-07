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

package org.eclipse.tractusx.traceability.integration.notification.alert;

import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.*;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository.JpaNotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.SENT;

class AlertsRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    JpaNotificationRepository jpaAlertRepository;

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository assetAsBuiltRepository;

    @Autowired
    NotificationRepository repository;

    @Test
    void givenAlertsWithAssets_whenCountDistinctAffectedSupplierPartsWithAlertReceivedStatus_thenReturnProperCount() {
        // Given
        assetsSupport.defaultAssetsStored();
        List<AssetAsBuiltEntity> assets = assetAsBuiltRepository.findAll();
        List<AssetAsBuiltEntity> ownAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.OWN))
                .toList();
        List<AssetAsBuiltEntity> supplierAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.SUPPLIER))
                .toList();
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, supplierAssets, null);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, supplierAssets, null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, ownAssets, null);
        assertThat(jpaAlertRepository.findAll()).hasSize(3);

        // When
        Long result = repository.countPartsByStatusAndOwnershipAndTypeAndNotificationType(List.of(NotificationStatus.RECEIVED), Owner.SUPPLIER, NotificationType.ALERT);

        // Then
        assertThat(result).isEqualTo(12);
    }

    @Test
    void givenAlertsWithAssets_whenCountDistinctAffectedOwnPartsWithAlertSentStatus_thenReturnProperCount() {
        // Given
        assetsSupport.defaultAssetsStored();
        List<AssetAsBuiltEntity> assets = assetAsBuiltRepository.findAll();
        List<AssetAsBuiltEntity> ownAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.OWN))
                .toList();
        List<AssetAsBuiltEntity> supplierAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.SUPPLIER))
                .toList();
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, supplierAssets, null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, ownAssets, null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, ownAssets, null);
        assertThat(jpaAlertRepository.findAll()).hasSize(3);

        // When
        long result = repository.countPartsByStatusAndOwnershipAndTypeAndNotificationType(List.of(NotificationStatus.SENT), Owner.OWN,NotificationType.ALERT);

        // Then
        assertThat(result).isEqualTo(1);
    }
}
