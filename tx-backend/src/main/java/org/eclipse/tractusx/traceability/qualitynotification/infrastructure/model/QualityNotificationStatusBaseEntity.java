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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model;


import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Set.of;


public enum QualityNotificationStatusBaseEntity {
    CREATED(QualityNotificationSideBaseEntity.SENDER, emptySet()),
    SENT(QualityNotificationSideBaseEntity.SENDER, Set.of(QualityNotificationSideBaseEntity.SENDER)),
    RECEIVED(QualityNotificationSideBaseEntity.RECEIVER, emptySet()),
    ACKNOWLEDGED(QualityNotificationSideBaseEntity.RECEIVER, Set.of(QualityNotificationSideBaseEntity.RECEIVER, QualityNotificationSideBaseEntity.SENDER)),
    ACCEPTED(QualityNotificationSideBaseEntity.RECEIVER, Set.of(QualityNotificationSideBaseEntity.RECEIVER)),
    DECLINED(QualityNotificationSideBaseEntity.RECEIVER, Set.of(QualityNotificationSideBaseEntity.RECEIVER)),
    CANCELED(QualityNotificationSideBaseEntity.SENDER, Set.of(QualityNotificationSideBaseEntity.SENDER)),
    CLOSED(QualityNotificationSideBaseEntity.SENDER, of(QualityNotificationSideBaseEntity.SENDER, QualityNotificationSideBaseEntity.RECEIVER));

    private static final Map<QualityNotificationStatusBaseEntity, Set<QualityNotificationStatusBaseEntity>> STATE_MACHINE;
    private static final Set<QualityNotificationStatusBaseEntity> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, QualityNotificationStatusBaseEntity> MAPPINGS;

    static {
        STATE_MACHINE = Map.of(
                CREATED, of(SENT, CANCELED),
                SENT, of(RECEIVED, CLOSED, ACKNOWLEDGED),
                RECEIVED, of(ACKNOWLEDGED, CLOSED),
                ACKNOWLEDGED, of(DECLINED, ACCEPTED, CLOSED),
                ACCEPTED, of(CLOSED),
                DECLINED, of(CLOSED),
                CLOSED, NO_TRANSITION_ALLOWED,
                CANCELED, NO_TRANSITION_ALLOWED
        );

        MAPPINGS = Arrays.stream(QualityNotificationStatusBaseEntity.values())
                .collect(Collectors.toMap(Enum::name, qualityNotificationStatusBaseEntity -> qualityNotificationStatusBaseEntity));
    }

    private final QualityNotificationSideBaseEntity qualityNotificationSideBaseEntity;
    private final Set<QualityNotificationSideBaseEntity> allowedTransitionFromSide;

    QualityNotificationStatusBaseEntity(QualityNotificationSideBaseEntity qualityNotificationSideBaseEntity, Set<QualityNotificationSideBaseEntity> allowedTransitionFromSide) {
        this.qualityNotificationSideBaseEntity = qualityNotificationSideBaseEntity;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static QualityNotificationStatusBaseEntity fromStringValue(String value) {
        return MAPPINGS.get(value);
    }

    public static QualityNotificationStatusBaseEntity from(QualityNotificationStatus status) {
        return QualityNotificationStatusBaseEntity.valueOf(status.name());
    }
    public static List<QualityNotificationStatusBaseEntity> from(List<QualityNotificationStatus> statuses) {
        return statuses.stream().map(QualityNotificationStatusBaseEntity::from).toList();
    }
}
