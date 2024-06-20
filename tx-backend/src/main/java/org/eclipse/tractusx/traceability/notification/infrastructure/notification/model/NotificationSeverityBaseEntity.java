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

package org.eclipse.tractusx.traceability.notification.infrastructure.notification.model;

import io.swagger.annotations.ApiModel;
import notification.request.NotificationSeverityRequest;


public enum NotificationSeverityBaseEntity {
    MINOR("MINOR"),
    MAJOR("MAJOR"),
    CRITICAL("CRITICAL"),
    LIFE_THREATENING("LIFE-THREATENING");

    private final String realName;

    public String getRealName() {
        return realName;
    }

    NotificationSeverityBaseEntity(String realName) {
        this.realName = realName;
    }

    public static NotificationSeverityBaseEntity fromString(String str) {
        for (NotificationSeverityBaseEntity s : NotificationSeverityBaseEntity.values()) {
            if (s.realName.equalsIgnoreCase(str)) {
                return s;
            }
        }
        return null;
    }

    public static NotificationSeverityBaseEntity from(NotificationSeverityRequest notificationSeverityRequest) {
        return NotificationSeverityBaseEntity.fromString(notificationSeverityRequest.getRealName());
    }
}
