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

import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSeverityBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository.JpaNotificationMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@Component
public class InvestigationNotificationsSupport {

    private static final String OWN_BPN = "BPNL00000001OWN";
    private static final String OWN_BPN_COMPANY_NAME = "Car Company";
    private static final String OTHER_BPN = "BPNL00000002OTHER";
    private static final String OTHER_BPN_COMPANY_NAME = "Parts Company";

    @Autowired
    JpaNotificationMessageRepository jpaNotificationMessageRepository;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    public void defaultInvestigationsStored() {
        storeCreatedInvestigations();
        storeReceivedInvestigations();
    }

    public NotificationMessageEntity storedNotification(NotificationMessageEntity notification) {
        return jpaNotificationMessageRepository.save(notification);
    }

    public void storedNotifications(NotificationMessageEntity... notifications) {
        Arrays.stream(notifications)
                .forEach(this::storedNotification);
    }


    public void assertNotificationsSize(int size) {
        List<NotificationMessageEntity> notifications = jpaNotificationMessageRepository.findAll();
        assertThat(notifications).hasSize(size);
    }


    private void storeCreatedInvestigations() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        Instant monthFromNow = Instant.parse("2023-11-10T10:10:10.00Z");
        LocalDate specificDate = LocalDate.of(2023, 11, 11);
        ZonedDateTime zonedDateTime = specificDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        NotificationEntity investigation1 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CREATED)
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .description("1")
                .createdDate(now.minus(3L, DAYS))
                .targetDate(instant)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .build();
        NotificationEntity investigation2 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.minus(2L, DAYS))
                .targetDate(instant)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .updated(now.minus(2L, DAYS))
                .build();
        NotificationEntity investigation3 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.minus(1L, DAYS))
                .updated(now.minus(1L, DAYS))
                .build();
        NotificationEntity investigation4 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("4")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now)
                .updated(now)
                .build();
        NotificationEntity investigation5 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("5")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now)
                .updated(now)
                .build();
        NotificationEntity investigation6 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("6")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plus(1L, DAYS))
                .updated(now.plus(1L, DAYS))
                .build();
        NotificationEntity investigation7 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("7")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plus(2L, DAYS))
                .updated(now.plus(2L, DAYS))
                .build();
        NotificationEntity investigation8 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("8")
                .side(NotificationSideBaseEntity.SENDER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plus(3L, DAYS))
                .updated(now.plus(3L, DAYS))
                .build();

        storedNotifications(
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(investigation1)
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
                        .notification(investigation2)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .id("3")
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(investigation3)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .id("4")
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(investigation4)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a4")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .id("5")
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(investigation5)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a5")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .id("6")
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .notification(investigation6)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a6")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .id("7")
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .notification(investigation7)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a7")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .id("8")
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .notification(investigation8)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a8")
                        .build()
        );
    }

    private void storeReceivedInvestigations() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        String otherBPN = "BPNL00000002OTHER";
        LocalDate specificDate = LocalDate.of(2023, 11, 10);
        ZonedDateTime zonedDateTime = specificDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        NotificationEntity investigation1 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .description("11")
                .targetDate(instant)
                .createdDate(now.minus(2L, DAYS))
                .build();
        NotificationEntity investigation2 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .type(NotificationTypeEntity.INVESTIGATION)
                .description("22")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minus(1L, DAYS))
                .build();
        NotificationEntity investigation3 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("33")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now)
                .build();
        NotificationEntity investigation4 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("44")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity investigation5 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plus(1L, DAYS))
                .build();
        NotificationEntity investigation6 = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .type(NotificationTypeEntity.INVESTIGATION)
                .createdDate(now.plus(2L, DAYS))
                .build();

        storedNotifications(
                NotificationMessageEntity
                        .builder()
                        .id("11")
                        .notification(investigation1)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a11")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("22")
                        .notification(investigation2)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a22")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("33")
                        .notification(investigation3)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a33")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("44")
                        .notification(investigation4)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a44")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("55")
                        .notification(investigation5)
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a55")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("66")
                        .notification(investigation6)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a66")
                        .build()
        );
    }

    public NotificationMessageEntity storeInvestigationNotification() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        NotificationEntity investigation = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .type(NotificationTypeEntity.INVESTIGATION)
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .build();
        NotificationMessageEntity notificationEntity = NotificationMessageEntity
                .builder()
                .status(NotificationStatusBaseEntity.SENT)
                .id("1")
                .createdBy(OWN_BPN)
                .createdByName(OWN_BPN_COMPANY_NAME)
                .sendTo(OTHER_BPN)
                .sendToName(OTHER_BPN_COMPANY_NAME)
                .notification(investigation)
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                .build();
        storedNotifications(notificationEntity);
        return jpaNotificationMessageRepository.findById(notificationEntity.getId()).get();
    }
}
