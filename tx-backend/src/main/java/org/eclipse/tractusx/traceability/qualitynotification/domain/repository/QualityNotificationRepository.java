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

package org.eclipse.tractusx.traceability.qualitynotification.domain.repository;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QualityNotificationRepository {

    PageResult<QualityNotification> findQualityNotificationsBySide(QualityNotificationSide notificationSide, Pageable pageable);

    Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId notificationId);

    Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId);

    long countQualityNotificationEntitiesByStatus(QualityNotificationStatus qualityNotificationStatus);

    long countQualityNotificationEntitiesBySide(QualityNotificationSide notificationSide);

    QualityNotificationId saveQualityNotificationEntity(QualityNotification notification);

    QualityNotificationId updateQualityNotificationEntity(QualityNotification notification);

    void updateQualityNotificationMessageEntity(QualityNotificationMessage notification);
}
