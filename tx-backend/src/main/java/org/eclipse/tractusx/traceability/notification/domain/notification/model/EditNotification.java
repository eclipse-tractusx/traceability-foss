/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.notification.domain.notification.model;

import lombok.Builder;
import lombok.Data;
import notification.request.EditNotificationRequest;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;

import java.time.Instant;
import java.util.List;



@Data
@Builder
public class EditNotification {

    private Long id;

    private String title;

    private String description;

    private Instant targetDate;

    private List<String> affectedPartIds;

    private String receiverBpn;

    private NotificationSeverity severity;

    public static EditNotification from(EditNotificationRequest editNotificationRequest, Long notificationId) {
        return EditNotification.builder()
                .id(notificationId)
                .title(editNotificationRequest.getTitle())
                .affectedPartIds(editNotificationRequest.getAffectedPartIds())
                .description(editNotificationRequest.getDescription())
                .targetDate(editNotificationRequest.getTargetDate())
                .severity(NotificationSeverity.fromString(editNotificationRequest.getSeverity() != null ? editNotificationRequest.getSeverity().getRealName() : null))
                .receiverBpn(editNotificationRequest.getReceiverBpn())
                .build();
    }


}
