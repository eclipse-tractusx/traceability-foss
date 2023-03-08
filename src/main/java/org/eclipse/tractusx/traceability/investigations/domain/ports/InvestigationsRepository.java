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

package org.eclipse.tractusx.traceability.investigations.domain.ports;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface InvestigationsRepository {
	InvestigationId save(Investigation investigation);
	InvestigationId update(Investigation investigation);
	PageResult<Investigation> getInvestigations(Set<InvestigationStatus> investigationStatuses, Pageable pageable);
	PageResult<Investigation> getInvestigations(InvestigationSide investigationSide, Pageable pageable);
	Optional<Investigation> findById(InvestigationId investigationId);
	void update(Notification notification);
	long countPendingInvestigations();
	Optional<Investigation> findByNotificationReferenceId(String notificationId);
	long countInvestigations(Set<InvestigationStatus> statuses);
	long countInvestigations(InvestigationSide investigationSide);
}
