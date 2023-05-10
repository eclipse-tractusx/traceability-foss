/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.qualitynotification.application.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface QualityNotificationService {
    QualityNotificationId startInvestigation(List<String> partIds, String description, Instant targetDate, String severity);

    PageResult<QualityNotification> getCreatedInvestigations(Pageable pageable);

    PageResult<QualityNotification> getReceivedInvestigations(Pageable pageable);

    QualityNotification findInvestigation(Long investigationId);

    QualityNotification loadInvestigationOrNotFoundException(QualityNotificationId investigationId);

    QualityNotification loadInvestigationByEdcNotificationIdOrNotFoundException(String edcNotificationId);

    void approveInvestigation(Long investigationId);

    void cancelInvestigation(Long investigationId);

    void updateInvestigation(Long investigationId, QualityNotificationStatus investigationStatus, String reason);
}
