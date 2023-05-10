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
package org.eclipse.tractusx.traceability.qualitynotification.application.response;

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Set.of;

public enum QualityNotificationStatusResponse {
    CREATED(QualityNotificationSideResponse.SENDER, emptySet()),
    SENT(QualityNotificationSideResponse.SENDER, Set.of(QualityNotificationSideResponse.SENDER)),
    RECEIVED(QualityNotificationSideResponse.RECEIVER, emptySet()),
    ACKNOWLEDGED(QualityNotificationSideResponse.RECEIVER, Set.of(QualityNotificationSideResponse.RECEIVER, QualityNotificationSideResponse.SENDER)),
    ACCEPTED(QualityNotificationSideResponse.RECEIVER, Set.of(QualityNotificationSideResponse.RECEIVER)),
    DECLINED(QualityNotificationSideResponse.RECEIVER, Set.of(QualityNotificationSideResponse.RECEIVER)),
    CANCELED(QualityNotificationSideResponse.SENDER, Set.of(QualityNotificationSideResponse.SENDER)),
    CLOSED(QualityNotificationSideResponse.SENDER, of(QualityNotificationSideResponse.SENDER, QualityNotificationSideResponse.RECEIVER));

    private static final Map<QualityNotificationStatusResponse, Set<QualityNotificationStatusResponse>> STATE_MACHINE;
    private static final Set<QualityNotificationStatusResponse> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, QualityNotificationStatusResponse> MAPPINGS;

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

        MAPPINGS = Arrays.stream(QualityNotificationStatusResponse.values())
                .collect(Collectors.toMap(Enum::name, qualityNotificationStatus -> qualityNotificationStatus));
    }

    private final QualityNotificationSideResponse qualityNotificationSide;
    private final Set<QualityNotificationSideResponse> allowedTransitionFromSide;

    QualityNotificationStatusResponse(QualityNotificationSideResponse qualityNotificationSide, Set<QualityNotificationSideResponse> allowedTransitionFromSide) {
        this.qualityNotificationSide = qualityNotificationSide;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static Optional<QualityNotificationStatusResponse> fromValue(String value) {
        return Optional.ofNullable(MAPPINGS.get(value));
    }

    public static QualityNotificationStatusResponse fromStringValue(String value) {
        return MAPPINGS.get(value);
    }

    public static QualityNotificationStatusResponse from(QualityNotificationStatus qualityNotificationStatus) {
        return QualityNotificationStatusResponse.fromStringValue(qualityNotificationStatus.name());
    }

    public boolean transitionAllowed(QualityNotificationStatusResponse to) {

        Set<QualityNotificationStatusResponse> allowedStatusesToTransition = STATE_MACHINE.get(this);

        if (!allowedStatusesToTransition.contains(to)) {
            return false;
        }

        return isSideEligibleForTransition(this, to);
    }

    private boolean isSideEligibleForTransition(QualityNotificationStatusResponse from, QualityNotificationStatusResponse to) {
        return to.allowedTransitionFromSide.contains(from.qualityNotificationSide);
    }
}
