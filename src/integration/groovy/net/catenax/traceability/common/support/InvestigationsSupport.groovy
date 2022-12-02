/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.common.support

import net.catenax.traceability.infrastructure.jpa.investigation.InvestigationEntity
import net.catenax.traceability.investigations.domain.model.InvestigationStatus

import java.time.Instant

trait InvestigationsSupport implements InvestigationsRepositoryProvider {

	void defaultReceivedInvestigationStored() {
		InvestigationEntity entity = new InvestigationEntity([], "BPN000001", InvestigationStatus.RECEIVED, "",  "some-description", Instant.now())

		storedInvestigation(entity)
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
