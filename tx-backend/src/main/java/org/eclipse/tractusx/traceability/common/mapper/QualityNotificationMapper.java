/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.common.mapper;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class QualityNotificationMapper {

    /**
     * Creates a QualityNotification object representing the notification received by the receiver for a given notification.
     *
     * @param bpn          the BPN of the notification
     * @param description  the description of the notification
     * @param notification the notification associated with the alert or investigation
     * @return an QualityNotification object representing the notification received by the receiver
     */
    public QualityNotification toQualityNotification(BPN bpn, String description, QualityNotificationMessage notification) {

        List<String> assetIds = new ArrayList<>();
        notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .forEach(assetIds::add);
        return QualityNotification.builder()
                .bpn(bpn)
                .notificationStatus(QualityNotificationStatus.RECEIVED)
                .notificationSide(QualityNotificationSide.RECEIVER)
                .description(description)
                .createdAt(Instant.now())
                .assetIds(assetIds)
                .notifications(List.of(notification))
                .build();
    }
}
