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

package org.eclipse.tractusx.traceability.notification.domain.notification.repository;

import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Optional<Notification> findOptionalNotificationById(NotificationId investigationId);

    Optional<Notification> findByEdcNotificationId(String edcNotificationId);

    long countNotificationsBySideAndType(NotificationSide investigationSide, NotificationType notificationType);

    NotificationId saveNotification(Notification investigation);

    void updateNotification(Notification investigation);

    void updateNotificationAndMessage(Notification notification);

    PageResult<Notification> getNotifications(Pageable pageable, SearchCriteria searchCriteria);

    long countOpenNotificationsByOwnershipAndNotificationType(List<Owner> owners, NotificationType notificationType);

    List<String> getDistinctFieldValues(String fieldName, List<String> startsWith, Integer resultLimit, NotificationSide owner);

    void updateErrorMessage(Notification notification, NotificationMessage message);

    void deleteByIdIn(List<String> messageIds);

    long countPartsByStatusAndOwnershipAndTypeAndNotificationType(List<NotificationStatus> received, Owner owner, NotificationType notificationType);
}
