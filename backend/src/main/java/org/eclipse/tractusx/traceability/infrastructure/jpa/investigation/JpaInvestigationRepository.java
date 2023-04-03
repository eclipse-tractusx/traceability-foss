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

package org.eclipse.tractusx.traceability.infrastructure.jpa.investigation;

import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaInvestigationRepository extends JpaRepository<InvestigationEntity, Long> {
	Page<InvestigationEntity> findAllByStatusInOrderByCreatedDesc(Set<InvestigationStatus> statuses, Pageable pageable);
	Page<InvestigationEntity> findAllBySideEqualsOrderByCreatedDesc(InvestigationSide investigationSide, Pageable pageable);
	long countAllByStatusEquals(InvestigationStatus status);
	long countAllBySideEquals(InvestigationSide investigationSide);
	long countAllByStatusIn(Set<InvestigationStatus> status);
	@Query("SELECT investigation FROM InvestigationEntity investigation JOIN investigation.notifications notification WHERE notification.id = :notificationId")
	Optional<InvestigationEntity> findByNotificationsNotificationId(String notificationId);
    @Query("SELECT investigation FROM InvestigationEntity investigation JOIN investigation.notifications notification WHERE notification.notificationReferenceId = :notificationReferenceId")
    Optional<InvestigationEntity> findByNotificationsNotificationReferenceId(String notificationReferenceId);
    @Query("SELECT investigation FROM InvestigationEntity investigation JOIN investigation.notifications notification WHERE notification.edcNotificationId = :edcNotificationid")
    Optional<InvestigationEntity> findByNotificationsEdcNotificationId(String edcNotificationid);
}
