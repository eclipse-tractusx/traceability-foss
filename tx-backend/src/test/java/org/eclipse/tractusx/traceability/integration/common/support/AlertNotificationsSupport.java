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

import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertNotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@Component
public class AlertNotificationsSupport {

    private static final String OWN_BPN = "BPNL00000001OWN";
    private static final String OWN_BPN_COMPANY_NAME = "Car Company";
    private static final String OTHER_BPN = "BPNL00000002OTHER";
    private static final String OTHER_BPN_COMPANY_NAME = "Parts Company";

    @Autowired
    JpaAlertNotificationRepository jpaAlertNotificationRepository;

    public void defaultAlertsStored() {
        storeCreatedAlerts();
        storeReceivedAlerts();
    }

    public AlertNotificationEntity storedAlertNotification(AlertNotificationEntity notification) {
        return jpaAlertNotificationRepository.save(notification);
    }

    public void storedAlertNotifications(AlertNotificationEntity... notifications) {
        Arrays.stream(notifications).forEach(this::storedAlertNotification);
    }

    public void assertAlertNotificationsSize(int size) {
        List<AlertNotificationEntity> notifications = jpaAlertNotificationRepository.findAll();

        assertThat(notifications).hasSize(size);
    }

    private void storeCreatedAlerts() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        Instant monthFromNow = Instant.parse("2023-11-10T10:10:10.00Z");

        AlertEntity alert1 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CREATED)
                .side(NotificationSideBaseEntity.SENDER)
                .description("1")
                .createdDate(now.minus(3L, DAYS))
                .build();
        AlertEntity alert2 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .build();
        AlertEntity alert3 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(1L, DAYS))
                .updated(now.minus(1L, DAYS))
                .build();
        AlertEntity alert4 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .updated(now)
                .build();
        AlertEntity alert5 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .acceptReason("Almighty demon king accepted this one")
                .description("5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .updated(now)
                .build();
        AlertEntity alert6 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .declineReason("Almighty demon king has declined this one")
                .description("6")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(1L, DAYS))
                .updated(now.plus(1L, DAYS))
                .build();
        AlertEntity alert7 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("7")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(2L, DAYS))
                .updated(now.plus(2L, DAYS))
                .build();
        AlertEntity alert8 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("8")
                .closeReason("Almighty demon king has closed that one")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(3L, DAYS))
                .updated(now.plus(3L, DAYS))
                .build();

        storedAlertNotifications(
                AlertNotificationEntity
                        .builder()
                        .id("1")
                        .alert(alert1)
                        .status(NotificationStatusBaseEntity.CREATED)
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .severity(QualityNotificationSeverity.MINOR)
                        .targetDate(monthFromNow.minus(3L, DAYS))
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .status(NotificationStatusBaseEntity.SENT)
                        .severity(QualityNotificationSeverity.MAJOR)
                        .id("2")
                        .targetDate(monthFromNow.minus(2L, DAYS))
                        .alert(alert2)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a2")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .id("3")
                        .sendTo(OWN_BPN)
                        .createdBy(OTHER_BPN)
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .targetDate(monthFromNow.minus(1L, DAYS))
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .alert(alert3)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a3")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .id("4")
                        .targetDate(monthFromNow)
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .alert(alert4)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a4")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .id("5")
                        .targetDate(monthFromNow)
                        .severity(QualityNotificationSeverity.MINOR)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .alert(alert5)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a5")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .id("6")
                        .severity(QualityNotificationSeverity.MAJOR)
                        .targetDate(monthFromNow.plus(1L, DAYS))
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .alert(alert6)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a6")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .id("7")
                        .targetDate(monthFromNow.plus(2L, DAYS))
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .alert(alert7)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a7")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .id("8")
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .targetDate(monthFromNow.plus(3L, DAYS))
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .alert(alert8)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a8")
                        .build()
        );
    }

    private void storeReceivedAlerts() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        AlertEntity alert1 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .description("11")
                .createdDate(now.minus(2L, DAYS))
                .build();
        AlertEntity alert2 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("22")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minus(1L, DAYS))
                .build();
        AlertEntity alert3 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("33")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        AlertEntity alert4 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("44")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        AlertEntity alert5 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(1L))
                .build();
        AlertEntity alert6 = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plus(2L, DAYS))
                .build();

        storedAlertNotifications(
                AlertNotificationEntity
                        .builder()
                        .id("11")
                        .alert(alert1)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a11")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("22")
                        .alert(alert2)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a22")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("33")
                        .alert(alert3)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a33")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("44")
                        .alert(alert4)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a44")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("55")
                        .alert(alert5)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a55")
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("66")
                        .alert(alert6)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a66")
                        .build()
        );
    }

    public AlertNotificationEntity storeAlertNotification() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        AlertEntity alert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .build();
        AlertNotificationEntity notificationEntity = AlertNotificationEntity
                .builder()
                .status(NotificationStatusBaseEntity.SENT)
                .id("1")
                .createdBy(OWN_BPN)
                .createdByName(OWN_BPN_COMPANY_NAME)
                .sendTo(OTHER_BPN)
                .alert(alert)
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                .build();
        storedAlertNotifications(notificationEntity);
        return jpaAlertNotificationRepository.findById(notificationEntity.getId()).get();
    }
}
