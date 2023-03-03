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
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationHeader;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

	@InjectMocks
	private NotificationMapper notificationMapper;

	@Test
	void testToReceiverNotification() {

		EDCNotificationHeader header = new EDCNotificationHeader("id123", "senderBPN", "senderAddress", "recipientBPN", "classification", "status", "relatedNotificationId", "RECEIVED", "2022-12-22");
		EDCNotificationContent content = new EDCNotificationContent("information", List.of("partId"));
		EDCNotification edcNotification = new EDCNotification(header, content);

		Notification expectedNotification = NotificationTestDataFactory.createNotificationTestData();

		Notification actualNotification = notificationMapper.toReceiverNotification(edcNotification);
		assertNotNull(actualNotification.getId());
		assertEquals(expectedNotification.getNotificationReferenceId(), actualNotification.getNotificationReferenceId());
		assertEquals(expectedNotification.getSenderBpnNumber(), actualNotification.getSenderBpnNumber());
		assertEquals(expectedNotification.getReceiverBpnNumber(), actualNotification.getReceiverBpnNumber());
		assertEquals(expectedNotification.getEdcUrl(), actualNotification.getEdcUrl());
		assertNull(actualNotification.getContractAgreementId());
		assertEquals(expectedNotification.getDescription(), actualNotification.getDescription());
		assertEquals(expectedNotification.getInvestigationStatus(), actualNotification.getInvestigationStatus());
		assertEquals(expectedNotification.getAffectedParts(), actualNotification.getAffectedParts());
	}
}
