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
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationStatusTransitionNotAllowed;
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
		InvestigationStatus status = CREATED;
		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.acknowledge();
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Accept Investigation with disallowed status")
	void forbidAcceptInvestigationWithDisallowedStatus() {

		InvestigationStatus status = CREATED;

		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.accept("some reason");
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Decline Investigation with disallowed status")
	void forbidDeclineInvestigationWithDisallowedStatus() {

		InvestigationStatus status = CREATED;

		investigation = receiverInvestigationWithStatus(status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.decline("some-reason");
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Acknowledge Investigation successfully")
	void acknowledgeInvestigationSuccessfully() {
		investigation = receiverInvestigationWithStatus(RECEIVED);
		investigation.acknowledge();
		assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Accept Investigation successfully")
	void acceptInvestigationSuccessfully() {
		investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
		investigation.accept("some reason");
		assertEquals(ACCEPTED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Decline Investigation successfully")
	void declineInvestigationSuccessfully() {

		investigation = receiverInvestigationWithStatus(ACKNOWLEDGED);
		investigation.decline("some reason");
		assertEquals(DECLINED, investigation.getInvestigationStatus());

	}


	//util functions
	private Investigation receiverInvestigationWithStatus(InvestigationStatus status) {
		return investigationWithStatus(status, InvestigationSide.RECEIVER);
	}

	private Investigation investigationWithStatus(InvestigationStatus status, InvestigationSide side) {
		BPN bpn = new BPN("BPNL000000000001");
		return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
	}
}
