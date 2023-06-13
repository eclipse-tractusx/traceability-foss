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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.BadRequestException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum NotificationType {
    QMINVESTIGATION("QM-Investigation"),
    QMALERT("QM-Alert");

    private static final Map<String, NotificationType> MAPPINGS;

    static {
        MAPPINGS = Arrays.stream(NotificationType.values())
                .collect(Collectors.toMap(NotificationType::getValue, notificationType -> notificationType));
    }

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public static Optional<NotificationType> fromValue(String value) {
        return Optional.ofNullable(MAPPINGS.get(value));
    }

    public static NotificationType from(QualityNotificationType qualityNotificationType) {
        if (qualityNotificationType.equals(QualityNotificationType.ALERT)) {
            return QMALERT;
        }
        if (qualityNotificationType.equals(QualityNotificationType.INVESTIGATION)) {
            return QMINVESTIGATION;
        }
        throw new BadRequestException("Wrong quality notification type wwas provided " + qualityNotificationType);
    }

    public String getValue() {
        return value;
    }
}
