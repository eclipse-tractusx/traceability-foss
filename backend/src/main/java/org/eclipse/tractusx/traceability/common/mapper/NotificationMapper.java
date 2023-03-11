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

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationMapper {

	/**
	 * Creates a Notification object representing the notification received by the receiver for a given EDCNotification.
	 *
	 * @param edcNotification the EDCNotification received by the receiver
	 * @return a Notification object representing the notification received by the receiver
	 */
	public Notification toReceiverNotification(EDCNotification edcNotification, InvestigationStatus investigationStatus) {
		return new Notification(
			UUID.randomUUID().toString(),
			edcNotification.getNotificationId(),
			edcNotification.getSenderBPN(),
			edcNotification.getRecipientBPN(),
			edcNotification.getSenderAddress(),
			null,
			edcNotification.getInformation(),
			investigationStatus,
			edcNotification.getListOfAffectedItems(),
			edcNotification.getTargetDate(),
			Severity.valueOf(edcNotification.getSeverity())
		);
	}
}
