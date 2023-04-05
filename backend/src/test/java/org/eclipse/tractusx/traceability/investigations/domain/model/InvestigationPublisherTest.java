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

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CREATED;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.RECEIVED;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.SENT;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CANCELED;
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CLOSED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InvestigationPublisherTest {

	Investigation investigation;

	@Test
	@DisplayName("Forbid Cancel Investigation with disallowed status")
	void forbidCancellingInvestigationWithDisallowedStatus() {

		InvestigationStatus status = RECEIVED;
		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.cancel(bpn));

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Send Investigation with disallowed status")
	void forbidSendingInvestigationWithDisallowedStatus() {

		InvestigationStatus status = SENT;
		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> investigation.send(bpn));

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Close Investigation for different BPN")
	void forbidCloseInvestigationWithDisallowedStatus() {

		InvestigationStatus status = CREATED;
		BPN bpn = new BPN("BPNL000000000001");
        BPN bpnOther = new BPN("BPNL12321321321");
		investigation = senderInvestigationWithStatus(bpnOther, status);

		assertThrows(InvestigationIllegalUpdate.class, () -> investigation.close(bpn, "some-reason"));

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Cancel Investigation for different BPN")
	void forbidCancelInvestigationForDifferentBpn() {

		InvestigationStatus status = CREATED;

		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);

		BPN bpn2 = new BPN("BPNL000000000002");

		assertThrows(InvestigationIllegalUpdate.class, () -> investigation.cancel(bpn2));

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Send Investigation for different BPN")
	void forbidSendInvestigationForDifferentBpn() {

		InvestigationStatus status = CREATED;

		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationIllegalUpdate.class, () -> investigation.send(new BPN("BPNL000000000002")));

		assertEquals(status, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Forbid Close Investigation for different BPN")
	void forbidCloseInvestigationForDifferentBpn() {

		InvestigationStatus status = CREATED;
		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);

		assertThrows(InvestigationIllegalUpdate.class, () -> investigation.close(new BPN("BPNL000000000002"), "some reason"));

		assertEquals(status, investigation.getInvestigationStatus());


	}

	@Test
	@DisplayName("Send Investigation status")
	void sendInvestigationSuccessfully() {

		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, CREATED);
		investigation.send(bpn);

		assertEquals(SENT, investigation.getInvestigationStatus());

	}

	@Test
	@DisplayName("Cancel Investigation status")
	void cancelInvestigationSuccessfully() {
		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, CREATED);
		investigation.cancel(bpn);


		assertEquals(CANCELED, investigation.getInvestigationStatus());
	}

	@Test
	@DisplayName("Close Investigation with allowed status")
	void closeInvestigationWithAllowedStatusSuccessfully() {

		InvestigationStatus status = SENT;

		BPN bpn = new BPN("BPNL000000000001");
		investigation = senderInvestigationWithStatus(bpn, status);
		investigation.close(bpn, "some-reason");

		assertEquals(CLOSED, investigation.getInvestigationStatus());

	}


	// util functions
	private Investigation senderInvestigationWithStatus(BPN bpn, InvestigationStatus status) {
		return investigationWithStatus(bpn, status, InvestigationSide.SENDER);
	}

	private Investigation investigationWithStatus(BPN bpn, InvestigationStatus status, InvestigationSide side) {
		return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", "", "", Instant.now(), new ArrayList<>(), new ArrayList<>());
	}
}

