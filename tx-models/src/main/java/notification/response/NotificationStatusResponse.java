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
package notification.response;
import io.swagger.annotations.ApiModel;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Set.of;

@ApiModel(description = "Describes status of notification")
public enum NotificationStatusResponse {
    CREATED(NotificationSideResponse.SENDER, emptySet()),
    SENT(NotificationSideResponse.SENDER, Set.of(NotificationSideResponse.SENDER)),
    RECEIVED(NotificationSideResponse.RECEIVER, emptySet()),
    ACKNOWLEDGED(NotificationSideResponse.RECEIVER, Set.of(NotificationSideResponse.RECEIVER, NotificationSideResponse.SENDER)),
    ACCEPTED(NotificationSideResponse.RECEIVER, Set.of(NotificationSideResponse.RECEIVER)),
    DECLINED(NotificationSideResponse.RECEIVER, Set.of(NotificationSideResponse.RECEIVER)),
    CANCELED(NotificationSideResponse.SENDER, Set.of(NotificationSideResponse.SENDER)),
    CLOSED(NotificationSideResponse.SENDER, of(NotificationSideResponse.SENDER, NotificationSideResponse.RECEIVER));

    private static final Map<NotificationStatusResponse, Set<NotificationStatusResponse>> STATE_MACHINE;
    private static final Set<NotificationStatusResponse> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, NotificationStatusResponse> MAPPINGS;

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

        MAPPINGS = Arrays.stream(NotificationStatusResponse.values())
                .collect(Collectors.toMap(Enum::name, qualityNotificationStatus -> qualityNotificationStatus));
    }

    private final NotificationSideResponse qualityNotificationSide;
    private final Set<NotificationSideResponse> allowedTransitionFromSide;

    NotificationStatusResponse(NotificationSideResponse qualityNotificationSide, Set<NotificationSideResponse> allowedTransitionFromSide) {
        this.qualityNotificationSide = qualityNotificationSide;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static NotificationStatusResponse fromStringValue(String value) {
        return MAPPINGS.get(value);
    }
}
