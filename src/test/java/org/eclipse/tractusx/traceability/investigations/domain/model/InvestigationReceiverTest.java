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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InvestigationReceiverTest {

	Investigation investigation;

	@Test
	@DisplayName("Forbid Acknowledge Investigation with disallowed status")
	void forbidAcknowledgeInvestigationWithDisallowedStatus() {
		InvestigationStatus status = CREATED;
		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.acknowledge(bpn);
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Accept Investigation with disallowed status")
	void forbidAcceptInvestigationWithDisallowedStatus() {

		InvestigationStatus status = CREATED;
		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.accept(bpn, "some reason");
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Decline Investigation with disallowed status")
	void forbidDeclineInvestigationWithDisallowedStatus() {

		InvestigationStatus status = CREATED;

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			investigation.decline(bpn, "some-reason");
		});

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Acknowledge Investigation with different bpn")
	void forbidAcknowledgeInvestigationWithDifferentBpn() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, RECEIVED);

		org.junit.jupiter.api.Assertions.assertThrows(InvestigationIllegalUpdate.class, () -> {
			investigation.acknowledge(new BPN("BPNL000000000002"));
		});

		assertEquals(RECEIVED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Accept Investigation with different bpn")
	void forbidAcceptInvestigationWithDifferentBpn() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED);

		org.junit.jupiter.api.Assertions.assertThrows(InvestigationIllegalUpdate.class, () -> {
			investigation.accept(new BPN("BPNL000000000002"), "some reason");
		});

		assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Decline Investigation with different bpn")
	void forbidDeclineInvestigationWithDifferentBpn() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, DECLINED);

		assertThrows(InvestigationIllegalUpdate.class, () -> {
			investigation.decline(new BPN("BPNL000000000002"), "some reason");
		});

		assertEquals(DECLINED, investigation.getInvestigationStatus());

	}


	@Test
	@DisplayName("Acknowledge Investigation successfully")
	void acknowledgeInvestigationSuccessfully() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, RECEIVED);

		investigation.acknowledge(bpn);


		assertEquals(ACKNOWLEDGED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Accept Investigation successfully")
	void acceptInvestigationSuccessfully() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED);
		investigation.accept(bpn, "some reason");


		assertEquals(ACCEPTED, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Decline Investigation successfully")
	void declineInvestigationSuccessfully() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED);
		investigation.decline(bpn, "some reason");


		assertEquals(DECLINED, investigation.getInvestigationStatus());

	}


	//util functions
	private Investigation receiverInvestigationWithStatus(BPN bpn, InvestigationStatus status) {
		return investigationWithStatus(bpn, status, InvestigationSide.RECEIVER);
	}

	private Investigation investigationWithStatus(BPN bpn, InvestigationStatus status, InvestigationSide side) {
		return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
	}
}
