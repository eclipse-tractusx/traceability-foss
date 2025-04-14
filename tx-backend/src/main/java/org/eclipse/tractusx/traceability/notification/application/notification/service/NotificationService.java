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
package org.eclipse.tractusx.traceability.notification.application.notification.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.EditNotification;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    NotificationId start(StartNotification startNotification);

    Notification find(Long notificationId);

    Notification loadOrNotFoundException(NotificationId notificationId);

    Notification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId);

    void approve(Long notificationId);

    void cancel(Long notificationId);

    void editNotification(EditNotification editNotification);

    void updateStatusTransition(Long notificationId, NotificationStatus notificationStatus, String reason);

    PageResult<Notification> getNotifications(Pageable pageable, SearchCriteria searchCriteria);

    List<String> getSearchableValues(String fieldName, List<String> startsWith, Integer size, NotificationSide side);
}
