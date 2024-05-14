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

package org.eclipse.tractusx.traceability.notification.domain.base.service;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static org.eclipse.tractusx.traceability.common.config.ApplicationProfiles.INTEGRATION_SPRING_BOOT;

@Slf4j
@Service
@Profile(INTEGRATION_SPRING_BOOT)
public class EdcNotificationMockServiceImpl implements EdcNotificationService {
    @Override
    public CompletableFuture<NotificationMessage> asyncNotificationMessageExecutor(NotificationMessage message) {
        log.info("EdcNotificationMockServiceImpl: {}", message);
        return CompletableFuture.completedFuture(message);
    }
}
