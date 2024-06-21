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

import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSeverityBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository.JpaNotificationMessageRepository;
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
    JpaNotificationMessageRepository jpaNotificationMessageRepository;

    public void defaultAlertsStored() {
        storeCreatedAlerts();
        storeReceivedAlerts();
    }

    public NotificationMessageEntity storedAlertNotification(NotificationMessageEntity notification) {
        return jpaNotificationMessageRepository.save(notification);
    }

    public void storedAlertNotifications(NotificationMessageEntity... notifications) {
        Arrays.stream(notifications).forEach(this::storedAlertNotification);
    }

    public void assertAlertNotificationsSize(int size) {
        List<NotificationMessageEntity> notifications = jpaNotificationMessageRepository.findAll();

        assertThat(notifications).hasSize(size);
    }

    private void storeCreatedAlerts() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        Instant monthFromNow = Instant.parse("2023-11-10T10:10:10.00Z");

        NotificationEntity alert1 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CREATED)
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .description("1")
                .targetDate(monthFromNow)
                .initialReceiverBpn("TESTBPN")
                .createdDate(now.minus(3L, DAYS))
                .build();
        NotificationEntity alert2 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .targetDate(monthFromNow)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .initialReceiverBpn("TESTBPN")
                .build();
        NotificationEntity alert3 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.minus(1L, DAYS))
                .initialReceiverBpn("TESTBPN")
                .updated(now.minus(1L, DAYS))
                .build();
        NotificationEntity alert4 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("4")
                .side(NotificationSideBaseEntity.SENDER)
                .initialReceiverBpn("TESTBPN")
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now)
                .updated(now)
                .build();
        NotificationEntity alert5 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("5")
                .side(NotificationSideBaseEntity.SENDER)
                .initialReceiverBpn("TESTBPN")
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now)
                .updated(now)
                .build();
        NotificationEntity alert6 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("6")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plus(1L, DAYS))
                .initialReceiverBpn("TESTBPN")
                .updated(now.plus(1L, DAYS))
                .build();
        NotificationEntity alert7 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("7")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plus(2L, DAYS))
                .initialReceiverBpn("TESTBPN")
                .updated(now.plus(2L, DAYS))
                .build();
        NotificationEntity alert8 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("8")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plus(3L, DAYS))
                .initialReceiverBpn("TESTBPN")
                .updated(now.plus(3L, DAYS))
                .build();

        storedAlertNotifications(
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(alert1)
                        .status(NotificationStatusBaseEntity.CREATED)
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .status(NotificationStatusBaseEntity.SENT)
                        .id("2")
                        .notification(alert2)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .id("3")
                        .sendTo(OWN_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(alert3)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .id("4")
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(alert4)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a4")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .id("5")
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(alert5)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a5")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .id("6")
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(alert6)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a6")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .id("7")
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .notification(alert7)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a7")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .id("8")
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .notification(alert8)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a8")
                        .build()
        );
    }

    private void storeReceivedAlerts() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        NotificationEntity alert1 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.ALERT)
                .description("11")
                .initialReceiverBpn("TESTBPN")
                .createdDate(now.minus(2L, DAYS))
                .build();
        NotificationEntity alert2 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("22")
                .side(NotificationSideBaseEntity.RECEIVER)
                .initialReceiverBpn("TESTBPN")
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.minus(1L, DAYS))
                .build();
        NotificationEntity alert3 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .initialReceiverBpn("TESTBPN")
                .description("33")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now)
                .build();
        NotificationEntity alert4 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("44")
                .initialReceiverBpn("TESTBPN")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity alert5 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("55")
                .initialReceiverBpn("TESTBPN")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plusSeconds(1L))
                .build();
        NotificationEntity alert6 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OTHER_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .initialReceiverBpn("TESTBPN")
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.plus(2L, DAYS))
                .build();

        storedAlertNotifications(
                NotificationMessageEntity
                        .builder()
                        .id("11")
                        .notification(alert1)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a11")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("22")
                        .notification(alert2)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a22")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("33")
                        .notification(alert3)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a33")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("44")
                        .notification(alert4)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a44")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("55")
                        .notification(alert5)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a55")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("66")
                        .notification(alert6)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a66")
                        .build()
        );
    }

    public NotificationMessageEntity storeAlertNotification() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        NotificationEntity alert = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .initialReceiverBpn("BPNTEST")
                .build();
        NotificationMessageEntity notificationEntity = NotificationMessageEntity
                .builder()
                .status(NotificationStatusBaseEntity.SENT)
                .id("1")
                .createdBy(OWN_BPN)
                .createdByName(OWN_BPN_COMPANY_NAME)
                .sendTo(OTHER_BPN)
                .notification(alert)
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                .build();
        storedAlertNotifications(notificationEntity);
        return jpaNotificationMessageRepository.findById(notificationEntity.getId()).get();
    }
}
