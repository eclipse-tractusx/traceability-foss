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

package org.eclipse.tractusx.traceability.investigations.domain.model;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InvestigationReceiverTest {

	Investigation investigation;

	@Test
	@DisplayName("Forbid Acknowledge Investigation with disallowed status")
	void forbidAcknowledgeInvestigationWithDisallowedStatus() {

		// Given
        Notification notification = testNotification();
        InvestigationStatus status = CREATED;
		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.acknowledge(notification);
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Accept Investigation with disallowed status")
	void forbidAcceptInvestigationWithDisallowedStatus() {
        Notification notification = testNotification();

		InvestigationStatus status = CREATED;

		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.accept("some reason", notification);
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Decline Investigation with disallowed status")
	void forbidDeclineInvestigationWithDisallowedStatus() {
        Notification notification = testNotification();

		InvestigationStatus status = CREATED;

		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.decline("some-reason", notification);
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Acknowledge Investigation successfully")
	void acknowledgeInvestigationSuccessfully() {
        Notification notification = testNotification();
        investigation = receiverInvestigationWithStatus(RECEIVED);
		investigation.acknowledge(notification);
		assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Accept Investigation successfully")
	void acceptInvestigationSuccessfully() {
        Notification notification = testNotification();
		investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
		investigation.accept("some reason", notification);
		assertEquals(ACCEPTED, investigation.getInvestigationStatus());
        assertEquals(ACCEPTED, notification.getInvestigationStatus());
	}

	@Test
	@DisplayName("Decline Investigation successfully")
	void declineInvestigationSuccessfully() {
        Notification notification = testNotification();
		investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
		investigation.decline("some reason", notification);
		assertEquals(DECLINED, investigation.getInvestigationStatus());
        assertEquals(DECLINED, notification.getInvestigationStatus());
	}

	//util functions
	private Investigation receiverInvestigationWithStatus(InvestigationStatus status) {
		return investigationWithStatus(status, InvestigationSide.RECEIVER);
	}

	private Investigation investigationWithStatus(InvestigationStatus status, InvestigationSide side) {
		BPN bpn = new BPN("BPNL000000000001");
		return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
	}

    private Notification testNotification() {
        return NotificationTestDataFactory.createNotificationTestData();
    }
}
