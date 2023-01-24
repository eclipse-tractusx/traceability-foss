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

import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACCEPTED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.ACKNOWLEDGED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.APPROVED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CANCELED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CLOSED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.CREATED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.DECLINED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.RECEIVED
import static org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus.SENT

class InvestigationReceiverSpec extends InvestigationBaseSpec {

	@Unroll
	def "should not allow to acknowledge investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.acknowledge(bpn)

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [CREATED, APPROVED, SENT, ACKNOWLEDGED, ACCEPTED, DECLINED, CLOSED, CANCELED]
	}

	@Unroll
	def "should not allow to accept investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.accept(bpn, "some reason")

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [CREATED, APPROVED, SENT, RECEIVED, ACCEPTED, DECLINED, CLOSED, CANCELED]
	}

	@Unroll
	def "should not allow to decline investigation with #investigationStatus status"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, investigationStatus)

		when:
			investigation.decline(bpn, "some reason")

		then:
			thrown(InvestigationStatusTransitionNotAllowed)

		and:
			investigation.getInvestigationStatus() == investigationStatus

		where:
			investigationStatus << [CREATED, APPROVED, SENT, RECEIVED, ACCEPTED, DECLINED, CLOSED, CANCELED]
	}

	def "should not allow to acknowledge investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, RECEIVED)

		when:
			investigation.acknowledge(new BPN("BPNL000000000002"))

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == RECEIVED
	}

	def "should not allow to accept investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED)

		when:
			investigation.accept(new BPN("BPNL000000000002"), "some reason")

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == ACKNOWLEDGED
	}

	def "should not allow to decline investigation for different bpn"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, DECLINED)

		when:
			investigation.decline(new BPN("BPNL000000000002"), "some reason")

		then:
			thrown(InvestigationIllegalUpdate)

		and:
			investigation.getInvestigationStatus() == DECLINED
	}

	def "should allow to acknowledge investigation"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, RECEIVED)

		when:
			investigation.acknowledge(bpn)

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == ACKNOWLEDGED
	}

	def "should allow to accept investigation"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED)

		when:
			investigation.accept(bpn, "some reason")

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == ACCEPTED
	}

	def "should allow to decline investigation"() {
		given:
			BPN bpn = new BPN("BPNL000000000001")

		and:
			Investigation investigation = receiverInvestigationWithStatus(bpn, ACKNOWLEDGED)

		when:
			investigation.decline(bpn, "some reason")

		then:
			noExceptionThrown()

		and:
			investigation.getInvestigationStatus() == DECLINED
	}
}
