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

package org.eclipse.tractusx.traceability.investigations.domain.model

import org.eclipse.tractusx.traceability.common.model.BPN
import spock.lang.Specification

import java.time.Instant

abstract class InvestigationBaseSpec extends Specification {

	protected Investigation senderInvestigationWithStatus(BPN bpn, InvestigationStatus status) {
		return investigationWithStatus(bpn, status, InvestigationSide.SENDER)
	}

	protected Investigation receiverInvestigationWithStatus(BPN bpn, InvestigationStatus status) {
		return investigationWithStatus(bpn, status, InvestigationSide.RECEIVER)
	}

	protected Investigation investigationWithStatus(BPN bpn, InvestigationStatus status, InvestigationSide side) {
		return new Investigation(new InvestigationId(1L), bpn, status, side, "", "", Instant.now(), [], [])
	}
}
