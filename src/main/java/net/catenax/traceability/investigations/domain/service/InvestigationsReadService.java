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

package net.catenax.traceability.investigations.domain.service;

import net.catenax.traceability.common.model.PageResult;
import net.catenax.traceability.investigations.adapters.rest.model.InvestigationData;
import net.catenax.traceability.investigations.domain.model.Investigation;
import net.catenax.traceability.investigations.domain.model.InvestigationId;
import net.catenax.traceability.investigations.domain.model.InvestigationStatus;
import net.catenax.traceability.investigations.domain.model.exception.InvestigationNotFoundException;
import net.catenax.traceability.investigations.domain.ports.InvestigationsRepository;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class InvestigationsReadService {

	private final InvestigationsRepository repository;

	public InvestigationsReadService(InvestigationsRepository repository) {
		this.repository = repository;
	}

	public InvestigationData findInvestigation(Long id) {
		InvestigationId investigationId = new InvestigationId(id);

		Investigation investigation = loadInvestigation(investigationId);

		return investigation.toData();
	}

	public PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable) {
		return getInvestigations(pageable, Investigation.CREATED_STATUSES);
	}

	public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
		return getInvestigations(pageable, Investigation.RECEIVED_STATUSES);
	}

	private PageResult<InvestigationData> getInvestigations(Pageable pageable, Set<InvestigationStatus> statuses) {
		List<InvestigationData> investigationData = repository.getInvestigations(statuses, pageable)
			.content()
			.stream()
			.sorted(Investigation.COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME)
			.map(Investigation::toData)
			.toList();

		Page<InvestigationData> investigationDataPage = new PageImpl<>(investigationData, pageable, repository.countInvestigations(statuses));

		return new PageResult<>(investigationDataPage);
	}

	public Investigation loadInvestigation(InvestigationId investigationId) {
		return repository.findById(investigationId)
			.orElseThrow(() -> new InvestigationNotFoundException(investigationId));
	}

	public Investigation loadInvestigationByNotificationReferenceId(String notificationId) {
		return repository.findByNotificationReferenceId(notificationId)
			.orElseThrow(() -> new InvestigationNotFoundException(notificationId));
	}
}
