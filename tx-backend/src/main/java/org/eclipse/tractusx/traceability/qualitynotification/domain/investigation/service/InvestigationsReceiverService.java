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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.AbstractQualityNotificationReceiverService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InvestigationsReceiverService extends AbstractQualityNotificationReceiverService {

    private final InvestigationRepository investigationsRepository;
    private final NotificationMessageMapper notificationMapper;
    private final QualityNotificationMapper qualityNotificationMapper;

    @Override
    protected QualityNotificationRepository getRepository() {
        return investigationsRepository;
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
        return new InvestigationNotFoundException(message);
    }

    @Override
    protected RuntimeException getIllegalUpdateException(String message) {
        return new InvestigationIllegalUpdate(message);
    }

}
