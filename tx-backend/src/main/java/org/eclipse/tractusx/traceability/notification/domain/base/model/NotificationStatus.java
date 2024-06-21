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

package org.eclipse.tractusx.traceability.notification.domain.base.model;

import notification.request.NotificationStatusRequest;
import notification.request.UpdateNotificationStatusRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Set.of;

public enum NotificationStatus {
    CREATED(NotificationSide.SENDER, emptySet()),
    SENT(NotificationSide.SENDER, Set.of(NotificationSide.SENDER)),
    RECEIVED(NotificationSide.RECEIVER, emptySet()),
    ACKNOWLEDGED(NotificationSide.RECEIVER, Set.of(NotificationSide.RECEIVER, NotificationSide.SENDER)),
    ACCEPTED(NotificationSide.RECEIVER, Set.of(NotificationSide.RECEIVER)),
    DECLINED(NotificationSide.RECEIVER, Set.of(NotificationSide.RECEIVER)),
    CANCELED(NotificationSide.SENDER, Set.of(NotificationSide.SENDER)),
    CLOSED(NotificationSide.SENDER, of(NotificationSide.SENDER, NotificationSide.RECEIVER));

    private static final Map<NotificationStatus, Set<NotificationStatus>> STATE_MACHINE;
    private static final Set<NotificationStatus> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, NotificationStatus> MAPPINGS;

    public static final List<NotificationStatus> ACTIVE_STATES = List.of(CREATED, SENT, RECEIVED, ACKNOWLEDGED, ACCEPTED, DECLINED);

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

        MAPPINGS = Arrays.stream(NotificationStatus.values())
                .collect(Collectors.toMap(Enum::name, notificationStatus -> notificationStatus));
    }

    private final NotificationSide notificationSide;
    private final Set<NotificationSide> allowedTransitionFromSide;

    NotificationStatus(NotificationSide notificationSide, Set<NotificationSide> allowedTransitionFromSide) {
        this.notificationSide = notificationSide;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static Optional<NotificationStatus> fromValue(String value) {
        return Optional.ofNullable(MAPPINGS.get(value));
    }

    public static NotificationStatus fromStringValue(String value) {
        return MAPPINGS.get(value);
    }

    public static NotificationStatus getPreviousStatus(NotificationStatus status) {
        return switch (status) {
            case CREATED, SENT, CANCELED -> NotificationStatus.CREATED;
            case ACKNOWLEDGED, RECEIVED, CLOSED -> NotificationStatus.SENT;
            case ACCEPTED, DECLINED -> NotificationStatus.ACKNOWLEDGED;
        };
    }
    public boolean transitionAllowed(NotificationStatus to) {

        Set<NotificationStatus> allowedStatusesToTransition = STATE_MACHINE.get(this);

        if (!allowedStatusesToTransition.contains(to)) {
            return false;
        }

        return isSideEligibleForTransition(this, to);
    }

    private boolean isSideEligibleForTransition(NotificationStatus from, NotificationStatus to) {
        return to.allowedTransitionFromSide.contains(from.notificationSide);
    }

    public boolean isActiveState() {
        return ACTIVE_STATES.contains(this);
    }


    public static NotificationStatus from(NotificationStatusRequest notificationStatusRequest) {
        return NotificationStatus.fromStringValue(notificationStatusRequest.name());
    }

    public static NotificationStatus from(UpdateNotificationStatusRequest updateNotificationStatusRequest) {
        return NotificationStatus.fromStringValue(updateNotificationStatusRequest.name());
    }
}
