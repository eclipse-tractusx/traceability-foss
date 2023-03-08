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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

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
		return getInvestigations(pageable, InvestigationSide.SENDER);
	}

	public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
		return getInvestigations(pageable, InvestigationSide.RECEIVER);
	}

	private PageResult<InvestigationData> getInvestigations(Pageable pageable, InvestigationSide investigationSide) {
		List<InvestigationData> investigationData = repository.getInvestigations(investigationSide, pageable)
			.content()
			.stream()
			.sorted(Investigation.COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME)
			.map(Investigation::toData)
			.toList();

		Page<InvestigationData> investigationDataPage = new PageImpl<>(investigationData, pageable, repository.countInvestigations(investigationSide));

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
