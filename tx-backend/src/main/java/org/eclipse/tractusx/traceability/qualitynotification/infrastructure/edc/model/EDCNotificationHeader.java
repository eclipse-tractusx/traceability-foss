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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.tractusx.traceability.common.model.EDC;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record EDCNotificationHeader(String notificationId, String senderBPN, String senderAddress, String recipientBPN,
                                    String classification, String severity, String relatedNotificationId,
                                    String status, String targetDate, String messageId) {


    @Override
    public String toString() {
        return "EDCNotificationHeader{" +
                "notificationId='" + EDC.sanitizer(notificationId) + '\'' +
                ", senderBPN='" + EDC.sanitizer(senderBPN) + '\'' +
                ", senderAddress='" + EDC.sanitizer(senderAddress) + '\'' +
                ", recipientBPN='" + EDC.sanitizer(recipientBPN) + '\'' +
                ", classification='" + EDC.sanitizer(classification) + '\'' +
                ", severity='" + EDC.sanitizer(severity) + '\'' +
                ", relatedNotificationId='" + EDC.sanitizer(relatedNotificationId) + '\'' +
                ", status='" + EDC.sanitizer(status) + '\'' +
                ", targetDate='" + EDC.sanitizer(targetDate) + '\'' +
                ", messageId='" + EDC.sanitizer(messageId) + '\'' +
                '}';
    }
}


