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

package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.common.model.*;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.*;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AlertTestDataFactory {

    public static AlertEntity[] createAlertEntitiesTestData(String senderBpn) {
        String createdDateInNovString = "12:00 PM, Thu 11/9/2023";
        String createdDateInDecString = "12:00 PM, Sat 12/9/2023";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant createdDateInNov = LocalDateTime.parse(createdDateInNovString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant createdDateInDec = LocalDateTime.parse(createdDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        AlertEntity firstAlert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("First Alert on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(createdDateInNov)
                .build();
        AlertEntity secondAlert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Second Alert on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(createdDateInNov)
                .build();
        AlertEntity thirdAlert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Alert on Asset3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(createdDateInNov)
                .build();
        AlertEntity fourthAlert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Fourth Alert on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(createdDateInDec)
                .build();
        AlertEntity fifthAlert = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Alert on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(createdDateInDec)
                .build();

        AlertEntity[] alertEntities = {firstAlert, secondAlert, thirdAlert, fourthAlert, fifthAlert};
        return alertEntities;
    }

    public static AlertNotificationEntity[] createAlertNotificationEntitiesTestData(String senderBpn) {

        AlertEntity[] alertEntities = createAlertEntitiesTestData(senderBpn);

        AlertNotificationEntity[] alertNotificationEntities = {
                AlertNotificationEntity
                        .builder()
                        .id("1")
                        .alert(alertEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.MAJOR)
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("2")
                        .alert(alertEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.MAJOR)
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("3")
                        .alert(alertEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("4")
                        .alert(alertEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build(),
                AlertNotificationEntity
                        .builder()
                        .id("5")
                        .alert(alertEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build()
        };

        return alertNotificationEntities;
    }
}
