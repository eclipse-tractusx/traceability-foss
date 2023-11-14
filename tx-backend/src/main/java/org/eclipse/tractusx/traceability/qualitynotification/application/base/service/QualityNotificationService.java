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
package org.eclipse.tractusx.traceability.qualitynotification.application.base.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;

public interface QualityNotificationService {

    QualityNotificationId start(StartQualityNotificationDomain startQualityAlertDomain);

    // TODO: Propose to upstream tractusx team to remove this method and use searchCriteria based method
    PageResult<QualityNotification> getCreated(Pageable pageable);

    PageResult<QualityNotification> getCreated(Pageable pageable, SearchCriteria searchCriteria);

    // TODO: Propose to upstream tractusx team to remove this method and use searchCriteria based method
    PageResult<QualityNotification> getReceived(Pageable pageable);

    PageResult<QualityNotification> getReceived(Pageable pageable, SearchCriteria searchCriteria);

    QualityNotification find(Long notificationId);

    QualityNotification loadOrNotFoundException(QualityNotificationId notificationId);

    QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId);

    void approve(Long notificationId);

    void cancel(Long notificationId);

    void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason);
}
