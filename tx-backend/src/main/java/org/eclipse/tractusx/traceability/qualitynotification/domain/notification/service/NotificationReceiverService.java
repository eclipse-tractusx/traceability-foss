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

package org.eclipse.tractusx.traceability.qualitynotification.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.AbstractQualityNotificationReceiverService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.exception.NotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.exception.NotificationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.repository.QualityNotificationRepository;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationReceiverService extends AbstractQualityNotificationReceiverService {

    private final NotificationRepository notificationRepository;
    private final NotificationMessageMapper notificationMapper;
    private final QualityNotificationMapper qualityNotificationMapper;


    @Override
    protected QualityNotificationRepository getRepository() {
        return notificationRepository;
    }

    @Override
    protected NotificationMessageMapper getNotificationMessageMapper() {
        return notificationMapper;
    }

    @Override
    protected QualityNotificationMapper getQualityNotificationMapper() {
        return qualityNotificationMapper;
    }

    @Override
    protected RuntimeException getNotFoundException(String message) {
        return new NotificationNotFoundException(message);
    }

    @Override
    protected RuntimeException getIllegalUpdateException(String message) {
        return new NotificationIllegalUpdate(message);
    }

}

