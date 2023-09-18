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


public enum NotificationStatusBaseEntity {
    CREATED(NotificationSideBaseEntity.SENDER, emptySet()),
    SENT(NotificationSideBaseEntity.SENDER, Set.of(NotificationSideBaseEntity.SENDER)),
    RECEIVED(NotificationSideBaseEntity.RECEIVER, emptySet()),
    ACKNOWLEDGED(NotificationSideBaseEntity.RECEIVER, Set.of(NotificationSideBaseEntity.RECEIVER, NotificationSideBaseEntity.SENDER)),
    ACCEPTED(NotificationSideBaseEntity.RECEIVER, Set.of(NotificationSideBaseEntity.RECEIVER)),
    DECLINED(NotificationSideBaseEntity.RECEIVER, Set.of(NotificationSideBaseEntity.RECEIVER)),
    CANCELED(NotificationSideBaseEntity.SENDER, Set.of(NotificationSideBaseEntity.SENDER)),
    CLOSED(NotificationSideBaseEntity.SENDER, of(NotificationSideBaseEntity.SENDER, NotificationSideBaseEntity.RECEIVER));

    private static final Map<NotificationStatusBaseEntity, Set<NotificationStatusBaseEntity>> STATE_MACHINE;
    private static final Set<NotificationStatusBaseEntity> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, NotificationStatusBaseEntity> MAPPINGS;

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

        MAPPINGS = Arrays.stream(NotificationStatusBaseEntity.values())
                .collect(Collectors.toMap(Enum::name, qualityNotificationStatusBaseEntity -> qualityNotificationStatusBaseEntity));
    }

    private final NotificationSideBaseEntity notificationSideBaseEntity;
    private final Set<NotificationSideBaseEntity> allowedTransitionFromSide;

    NotificationStatusBaseEntity(NotificationSideBaseEntity notificationSideBaseEntity, Set<NotificationSideBaseEntity> allowedTransitionFromSide) {
        this.notificationSideBaseEntity = notificationSideBaseEntity;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static NotificationStatusBaseEntity fromStringValue(String value) {
        return MAPPINGS.get(value);
    }

    public static NotificationStatusBaseEntity from(QualityNotificationStatus status) {
        return NotificationStatusBaseEntity.valueOf(status.name());
    }
    public static List<NotificationStatusBaseEntity> from(List<QualityNotificationStatus> statuses) {
        return statuses.stream().map(NotificationStatusBaseEntity::from).toList();
    }
}
