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

package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.InvestigationEntity
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus

import java.time.Instant

trait InvestigationsSupport implements InvestigationsRepositoryProvider {

	Long defaultReceivedInvestigationStored() {
		InvestigationEntity entity = new InvestigationEntity([], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "",  "some-description", Instant.now())

		return storedInvestigation(entity)
	}

	Long defaultAcknowledgedInvestigationStored() {
		InvestigationEntity entity = new InvestigationEntity([], "BPNL00000003AXS3", InvestigationStatus.ACKNOWLEDGED, InvestigationSide.RECEIVER, "",  "", Instant.now())

		return storedInvestigation(entity)
	}

	void assertInvestigationsSize(int size) {
		List<InvestigationEntity> investigations = jpaInvestigationRepository().findAll()

		assert investigations.size() == size
	}

	void assertInvestigationStatus(InvestigationStatus investigationStatus) {
		jpaInvestigationRepository().findAll().each {
			assert it.status == investigationStatus
		}
	}

	void storedInvestigations(InvestigationEntity... investigations) {
		investigations.each {
			jpaInvestigationRepository().save(it)
		}
	}

	Long storedInvestigation(InvestigationEntity investigation) {
		return jpaInvestigationRepository().save(investigation).id
	}
}
