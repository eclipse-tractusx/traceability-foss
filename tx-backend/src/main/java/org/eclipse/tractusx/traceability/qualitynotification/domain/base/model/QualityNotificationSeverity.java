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

package org.eclipse.tractusx.traceability.qualitynotification.domain.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import qualitynotification.base.request.QualityNotificationSeverityRequest;

@ApiModel(description = "Describes the criticality of a notification")
public enum QualityNotificationSeverity {
    MINOR("MINOR"),
    MAJOR("MAJOR"),
    CRITICAL("CRITICAL"),
    @ApiModelProperty(name = "LIFE-THREATENING")
    LIFE_THREATENING("LIFE-THREATENING");

    private final String realName;

    QualityNotificationSeverity(String realName) {
        this.realName = realName;
    }

    public static QualityNotificationSeverity fromString(String str) {
        for (QualityNotificationSeverity s : QualityNotificationSeverity.values()) {
            if (s.realName.equalsIgnoreCase(str)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant " + QualityNotificationSeverity.class.getCanonicalName() + "." + str);
    }

    public String getRealName() {
        return realName;
    }

    public static QualityNotificationSeverity from(QualityNotificationSeverityRequest qualityNotificationSeverityRequest) {
        return QualityNotificationSeverity.fromString(qualityNotificationSeverityRequest.getRealName());
    }
}
