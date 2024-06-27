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

import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository.JpaNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class AlertsSupport {

    @Autowired
    JpaNotificationRepository jpaNotificationRepository;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    public Long defaultReceivedAlertStored() {
        NotificationEntity entity = NotificationEntity.builder()
                .title("test")
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.ALERT)
                .initialReceiverBpn("TESTBPN")
                .description("some description")
                .createdDate(Instant.now())
                .build();

        return storedAlert(entity);
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
                .initialReceiverBpn("BPNL00000003AXS4")
                .build();
        Long alertId = storedAlert(entity);
        NotificationEntity savedAlert = jpaNotificationRepository.findById(alertId).get();
        savedAlert.setAssets(assetsAsBuilt);
        jpaNotificationRepository.save(savedAlert);
        return alertId;
    }

    public void assertAlertsSize(int size) {
        List<NotificationEntity> alerts = jpaNotificationRepository.findAll();

        assert alerts.size() == size;
    }

    public void assertAlertStatus(NotificationStatus alertStatus) {
        jpaNotificationRepository.findAll().forEach(alert ->
                assertThat(alert.getStatus().name()).isEqualTo(alertStatus.name())
        );
    }

    void storedAlerts(NotificationEntity... alerts) {
        jpaNotificationRepository.saveAll(Arrays.asList(alerts));
    }

    public Long storedAlert(NotificationEntity alert) {
        return jpaNotificationRepository.save(alert).getId();
    }

    public NotificationEntity storedAlertFullObject(NotificationEntity alert) {
        return jpaNotificationRepository.save(alert);
    }
}
