/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository.JpaNotificationRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class NotificationSupport {

    private final JpaNotificationRepository jpaNotificationRepository;

    public Long defaultReceivedInvestigationStored() {
        NotificationEntity entity = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .type(NotificationTypeEntity.INVESTIGATION)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .description("some description")
                .createdDate(Instant.now())
                .build();

        return jpaNotificationRepository.save(entity).getId();
    }

    public Long storeAlertWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt) {
        return storeAlertWithStatusAndAssets(status, assetsAsBuilt, NotificationSideBaseEntity.RECEIVER);
    }

    public Long storeAlertWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt, NotificationSideBaseEntity side) {
        NotificationEntity entity = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(status)
                .side(side)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(Instant.now())
                .build();
        Long alertId = storedAlert(entity);
        NotificationEntity savedAlert = jpaNotificationRepository.findById(alertId).get();
        savedAlert.setAssets(assetsAsBuilt);
        jpaNotificationRepository.save(savedAlert);
        return alertId;
    }

    public Long storedAlert(NotificationEntity alert) {
        return jpaNotificationRepository.save(alert).getId();
    }

    public void assertInvestigationsSize(int size) {
        List<NotificationEntity> investigations = jpaNotificationRepository.findAll();

        assertThat(investigations).hasSize(size);
    }

    public void assertInvestigationStatus(QualityNotificationStatus investigationStatus) {
        jpaNotificationRepository.findAll().forEach(
                investigation -> assertThat(investigation.getStatus().name()).isEqualTo(investigationStatus.name())
        );
    }
}
