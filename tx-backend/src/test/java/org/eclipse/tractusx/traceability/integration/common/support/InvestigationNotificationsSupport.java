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
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository.JpaInvestigationNotificationRepository;
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
public class InvestigationNotificationsSupport {

    private static final String OWN_BPN = "BPNL00000001OWN";
    private static final String OWN_BPN_COMPANY_NAME = "Car Company";
    private static final String OTHER_BPN = "BPNL00000002OTHER";
    private static final String OTHER_BPN_COMPANY_NAME = "Parts Company";

    @Autowired
    JpaInvestigationNotificationRepository jpaInvestigationNotificationRepository;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    public void defaultInvestigationsStored() {
        storeCreatedInvestigations();
        storeReceivedInvestigations();
    }

    public InvestigationNotificationEntity storedNotification(InvestigationNotificationEntity notification) {
        return jpaInvestigationNotificationRepository.save(notification);
    }

    public void storedNotifications(InvestigationNotificationEntity... notifications) {
        Arrays.stream(notifications)
                .forEach(this::storedNotification);
    }


    public void assertNotificationsSize(int size) {
        List<InvestigationNotificationEntity> notifications = jpaInvestigationNotificationRepository.findAll();
        assertThat(notifications).hasSize(size);
    }


    private void storeCreatedInvestigations() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        Instant monthFromNow = Instant.parse("2023-11-10T10:10:10.00Z");

        InvestigationEntity investigation1 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CREATED)
                .side(NotificationSideBaseEntity.SENDER)
                .description("1")
                .createdDate(now.minus(3L, DAYS))
                .build();
        InvestigationEntity investigation2 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .build();
        InvestigationEntity investigation3 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(1L, DAYS))
                .updated(now.minus(1L, DAYS))
                .build();
        InvestigationEntity investigation4 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .updated(now)
                .build();
        InvestigationEntity investigation5 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .acceptReason("Almighty demon king accepted this one")
                .description("5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .updated(now)
                .build();
        InvestigationEntity investigation6 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .declineReason("Almighty demon king has declined this one")
                .description("6")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(1L, DAYS))
                .updated(now.plus(1L, DAYS))
                .build();
        InvestigationEntity investigation7 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("7")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(2L, DAYS))
                .updated(now.plus(2L, DAYS))
                .build();
        InvestigationEntity investigation8 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("8")
                .closeReason("Almighty demon king has closed that one")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plus(3L, DAYS))
                .updated(now.plus(3L, DAYS))
                .build();

        storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(investigation1)
                        .status(NotificationStatusBaseEntity.CREATED)
                        .severity(QualityNotificationSeverity.MINOR)
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .targetDate(monthFromNow.minus(3L, DAYS))
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .status(NotificationStatusBaseEntity.SENT)
                        .severity(QualityNotificationSeverity.MAJOR)
                        .id("2")
                        .targetDate(monthFromNow.minus(2L, DAYS))
                        .investigation(investigation2)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a2")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .id("3")
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .targetDate(monthFromNow.minus(1L, DAYS))
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .investigation(investigation3)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a3")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .id("4")
                        .targetDate(monthFromNow)
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .investigation(investigation4)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a4")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .severity(QualityNotificationSeverity.MINOR)
                        .id("5")
                        .targetDate(monthFromNow)
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .investigation(investigation5)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a5")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .severity(QualityNotificationSeverity.MAJOR)
                        .targetDate(monthFromNow.plus(1L, DAYS))
                        .id("6")
                        .sendTo(OTHER_BPN)
                        .createdBy(OTHER_BPN)
                        .createdByName(OTHER_BPN_COMPANY_NAME)
                        .investigation(investigation6)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a6")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .id("7")
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .targetDate(monthFromNow.plus(2L, DAYS))
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .investigation(investigation7)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a7")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .targetDate(monthFromNow.plus(3L, DAYS))
                        .id("8")
                        .sendTo(OTHER_BPN)
                        .createdBy(OWN_BPN)
                        .createdByName(OWN_BPN_COMPANY_NAME)
                        .investigation(investigation8)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a8")
                        .build()
        );
    }

    private void storeReceivedInvestigations() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");
        String otherBPN = "BPNL00000002OTHER";

        InvestigationEntity investigation1 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .description("11")
                .createdDate(now.minus(2L, DAYS))
                .build();
        InvestigationEntity investigation2 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("22")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minus(1L, DAYS))
                .build();
        InvestigationEntity investigation3 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("33")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        InvestigationEntity investigation4 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.DECLINED)
                .description("44")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity investigation5 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plus(1L, DAYS))
                .build();
        InvestigationEntity investigation6 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(otherBPN)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("55")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plus(2L, DAYS))
                .build();

        storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("11")
                        .investigation(investigation1)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a11")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("22")
                        .investigation(investigation2)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a22")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("33")
                        .investigation(investigation3)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a33")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("44")
                        .investigation(investigation4)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.DECLINED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a44")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("55")
                        .investigation(investigation5)
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a55")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("66")
                        .investigation(investigation6)
                        .createdBy(OTHER_BPN)
                        .status(NotificationStatusBaseEntity.CLOSED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a66")
                        .build()
        );
    }

    public InvestigationNotificationEntity storeInvestigationNotification() {
        Instant now = Instant.parse("2023-10-10T10:10:10.00Z");

        InvestigationEntity investigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(OWN_BPN)
                .status(NotificationStatusBaseEntity.SENT)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .updated(now.minus(2L, DAYS))
                .build();
        InvestigationNotificationEntity notificationEntity = InvestigationNotificationEntity
                .builder()
                .status(NotificationStatusBaseEntity.SENT)
                .id("1")
                .createdBy(OWN_BPN)
                .createdByName(OWN_BPN_COMPANY_NAME)
                .sendTo(OTHER_BPN)
                .sendToName(OTHER_BPN_COMPANY_NAME)
                .investigation(investigation)
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a1")
                .build();
        storedNotifications(notificationEntity);
        return jpaInvestigationNotificationRepository.findById(notificationEntity.getId()).get();
    }
}
