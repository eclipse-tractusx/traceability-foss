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

    @Autowired
    JpaInvestigationNotificationRepository jpaInvestigationNotificationRepository;

    public void defaultInvestigationsStored() {
        Instant now = Instant.now();
        String testBpn = "BPNL00000003AXS3";
        String testBpn2 = "BPNL00000003AXS2";

        InvestigationEntity investigation1 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(NotificationStatusBaseEntity.SENT)
                .side(NotificationSideBaseEntity.SENDER)
                .description("1")
                .createdDate(now.minusSeconds(10L))
                .build();
        InvestigationEntity investigation2 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        InvestigationEntity investigation3 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        InvestigationEntity investigation4 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity investigation5 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minus(2L, DAYS))
                .build();
        InvestigationEntity investigation6 = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn2)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("6")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minus(2L, DAYS))
                .build();


        storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(investigation1)
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a11")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CREATED)
                        .id("2")
                        .investigation(investigation2)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a22")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CREATED)
                        .id("3")
                        .investigation(investigation3)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a33")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CREATED)
                        .id("4")
                        .investigation(investigation4)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a44")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.CREATED)
                        .id("5")
                        .investigation(investigation5)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a55")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .id("6")
                        .investigation(investigation6)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a66")
                        .build()
        );
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
}
