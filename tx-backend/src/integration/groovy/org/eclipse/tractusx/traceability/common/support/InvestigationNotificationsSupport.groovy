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

package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity

trait InvestigationNotificationsSupport implements InvestigationNotificationRepositoryProvider {

    InvestigationNotificationEntity storedNotification(InvestigationNotificationEntity notification) {
        return jpaNotificationRepository().save(notification)
    }

    void storedNotifications(InvestigationNotificationEntity... notifications) {
        notifications.each {
            storedNotification(it)
        }
    }

    void assertNotificationsSize(int size) {
        List<InvestigationNotificationEntity> notifications = jpaNotificationRepository().findAll()

        assert notifications.size() == size
    }

    void assertNotifications(Closure closure) {
        jpaNotificationRepository().findAll().each closure
    }

}
