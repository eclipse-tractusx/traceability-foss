/********************************************************************************
 * Copyright (c) 20234Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository;

import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository.JpaNotificationMessageRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository.JpaNotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository.NotificationRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationsRepositoryImplTest {

    @InjectMocks
    private NotificationRepositoryImpl investigationsRepository;

    @Mock
    private JpaNotificationRepository jpaNotificationRepository;

    @Mock
    private JpaNotificationMessageRepository jpaNotificationMessageRepository;

    @Mock
    private JpaAssetAsBuiltRepository assetsRepository;

    @Mock
    private Clock clock;

    @Test
    void updateErrorMessage() {

        // Given
        QualityNotificationMessage message = QualityNotificationMessage.builder().notificationStatus(QualityNotificationStatus.ACKNOWLEDGED).affectedParts(List.of(new QualityNotificationAffectedPart("123"))).build();
        QualityNotification qualityNotification = QualityNotification.builder().notificationStatus(QualityNotificationStatus.ACKNOWLEDGED).assetIds(List.of("123")).notificationId(new QualityNotificationId(123L)).bpn(BPN.of("ABC")).notifications(List.of(message)).build();
        AssetAsBuiltEntity assetAsBuiltEntity = AssetAsBuiltEntity.builder().id("123").build();
        NotificationEntity entity = NotificationEntity.builder().assets(List.of(assetAsBuiltEntity)).build();
        NotificationMessageEntity notificationEntity = NotificationMessageEntity.from(entity, message, List.of(assetAsBuiltEntity));
        when(assetsRepository.findByIdIn(any())).thenReturn(List.of(assetAsBuiltEntity));
        when(jpaNotificationRepository.findById(any())).thenReturn(Optional.of(entity));
        when(jpaNotificationMessageRepository.findById(notificationEntity.getId())).thenReturn(Optional.of(notificationEntity));
        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        // When
        investigationsRepository.updateErrorMessage(qualityNotification);
        // Then
        verify(jpaNotificationMessageRepository, times(1)).save(any());

    }
}
