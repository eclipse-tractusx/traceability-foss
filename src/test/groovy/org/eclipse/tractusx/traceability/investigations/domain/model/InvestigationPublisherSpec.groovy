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
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationStatusTransitionNotAllowed
import spock.lang.Unroll

import static InvestigationStatus.ACCEPTED
import static InvestigationStatus.ACKNOWLEDGED
import static InvestigationStatus.APPROVED
import static InvestigationStatus.CANCELED
import static InvestigationStatus.CLOSED
import static InvestigationStatus.CREATED
import static InvestigationStatus.DECLINED
import static InvestigationStatus.RECEIVED
import static InvestigationStatus.SENT

class InvestigationPublisherSpec extends InvestigationBaseSpec {

	@Unroll
	def "should not allow to cancel investigation with #investigationStatus status"() {
		given:
            BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.cancel(bpn)

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [RECEIVED, ACKNOWLEDGED, DECLINED, ACCEPTED, CLOSED]
	}

	@Unroll
	def "should not allow to approve investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.approve(bpn)

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [APPROVED, SENT, RECEIVED, ACKNOWLEDGED, ACCEPTED, DECLINED, CLOSED]
	}

	@Unroll
	def "should not allow to close investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.close(bpn, "some-reason")

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [CREATED, CLOSED, CANCELED]
	}

	def "should not allow to cancel investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.cancel(new BPN("BPNL000000000002"))

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << InvestigationStatus.values().toList()
	}

	def "should not allow to approve investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.approve(new BPN("BPNL000000000002"))

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << InvestigationStatus.values().toList()
	}

	def "should not allow to close investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.close(new BPN("BPNL000000000002"), "some reason")

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << InvestigationStatus.values().toList()
	}

	def "should allow to approve investigation"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, CREATED)

		when:
			investigation.approve(bpn)

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == APPROVED
	}

	def "should allow to cancel investigation status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, CREATED)

		when:
			investigation.cancel(bpn)

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == CANCELED
	}

	@Unroll
	def "should allow to close investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = senderInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.close(bpn, "some-reason")

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == CLOSED

		where:
			investigationStatus << [APPROVED, SENT, RECEIVED, ACKNOWLEDGED, ACCEPTED, DECLINED]
	}
}
