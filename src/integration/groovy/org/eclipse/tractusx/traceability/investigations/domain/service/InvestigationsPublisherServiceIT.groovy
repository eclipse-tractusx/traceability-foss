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

package org.eclipse.tractusx.traceability.investigations.domain.service


import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class InvestigationsPublisherServiceIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, NotificationsSupport, BpnSupport {

	@Autowired
	InvestigationsReceiverService investigationsReceiverService

	@Transactional
	def "should receive notification"() {
		given:
			defaultAssetsStored()

		and:
            EDCNotification notification = new EDCNotification(
				"it",
				new Notification(
					"some-id",
					null,
					"bpn-a",
					"BPNL00000003AXS3",
					"edcUrl",
					null,
					"description",
                        InvestigationStatus.APPROVED,
					[new AffectedPart("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")]
				)
			)

		when:
			investigationsReceiverService.handle(notification)

		then:
			assertInvestigationsSize(1)
			assertNotificationsSize(1)
	}

}
