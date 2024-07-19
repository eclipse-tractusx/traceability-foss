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

package org.eclipse.tractusx.traceability.notification.application.notification.validation;

import lombok.experimental.UtilityClass;
import notification.request.UpdateNotificationStatusTransitionRequest;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;

import java.util.Set;

import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.ACCEPTED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.DECLINED;

@UtilityClass
public class UpdateNotificationValidator {

    private static final Set<NotificationStatus> ALLOWED_STATUSES = Set.of(ACKNOWLEDGED, ACCEPTED, DECLINED);

    private static final int MINIMUM_REASON_CHARACTERS_SIZE = 15;
    private static final int MAXIMUM_REASON_CHARACTERS_SIZE = 1000;

    public static void validate(UpdateNotificationStatusTransitionRequest updateInvestigationRequest) {
        NotificationStatus status = NotificationStatus.fromStringValue(updateInvestigationRequest.getStatus().name());

        if (!ALLOWED_STATUSES.contains(status)) {
            throw new UpdateNotificationValidationException("%s not allowed for update investigation with".formatted(status));
        }

        String reason = updateInvestigationRequest.getReason();

        if (status == ACKNOWLEDGED && (reason != null && !reason.isBlank())) {
            throw new UpdateNotificationValidationException("Update investigation reason can't be present for %s status".formatted(ACKNOWLEDGED));

        }

        if (status != ACKNOWLEDGED) {
            if (reason == null || reason.isBlank()) {
                throw new UpdateNotificationValidationException("Update investigation reason must be present");
            }

            if (reason.length() < MINIMUM_REASON_CHARACTERS_SIZE || reason.length() > MAXIMUM_REASON_CHARACTERS_SIZE) {
                throw new UpdateNotificationValidationException("Reason should have at least %d characters and at most %d characters".formatted(MINIMUM_REASON_CHARACTERS_SIZE, MAXIMUM_REASON_CHARACTERS_SIZE));
            }
        }
    }
}
