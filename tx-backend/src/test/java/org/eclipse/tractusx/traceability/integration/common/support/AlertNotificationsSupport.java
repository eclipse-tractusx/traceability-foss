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

package org.eclipse.tractusx.traceability.integration.common.support;

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class AlertNotificationsSupport {

    @Autowired
    JpaAlertNotificationRepository jpaAlertNotificationRepository;

    public AlertNotificationEntity storedAlertNotification(AlertNotificationEntity notification) {
        return jpaAlertNotificationRepository.save(notification);
    }

    public void storedAlertNotifications(AlertNotificationEntity... notifications) {
        Arrays.stream(notifications).forEach(this::storedAlertNotification);
    }

    public void assertAlertNotificationsSize(int size) {
        List<AlertNotificationEntity> notifications = jpaAlertNotificationRepository.findAll();

        assertThat(notifications).hasSize(size);
    }
}
