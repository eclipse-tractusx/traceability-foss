/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository;

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {

    long countAllBySideEquals(NotificationSideBaseEntity notificationSide);

    long countAllBySideEqualsAndTypeEquals(NotificationSideBaseEntity notificationSide, NotificationTypeEntity notificationType);

    @Query("SELECT notification FROM NotificationEntity notification JOIN notification.messages notificationMessage WHERE notificationMessage.edcNotificationId = :edcNotificationId")
    Optional<NotificationEntity> findByNotificationMessageEdcNotificationId(@Param("edcNotificationId") String edcNotificationId);

    @Query("SELECT notification FROM NotificationEntity notification JOIN notification.messages notificationMessage WHERE notificationMessage.id = :id")
    Optional<NotificationEntity> findByNotificationMessageId(@Param("id") String id);

    List<NotificationEntity> findAllByStatusIn(List<NotificationStatusBaseEntity> statuses);

    List<NotificationEntity> findAllByStatusInAndType(List<NotificationStatusBaseEntity> statuses, NotificationTypeEntity type);
}
