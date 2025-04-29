/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.domain;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class EnumFieldUtils {

    public List<String> getValues(String fieldName, List<String> startsWith) {
        return switch (fieldName) {
            case "owner" -> filterEnumValues(Owner.values(), startsWith);
            case "qualityType" -> filterEnumValues(QualityType.values(), startsWith);
            case "semanticDataModel" -> filterEnumValues(SemanticDataModel.values(), startsWith);
            case "importState" -> filterEnumValues(ImportState.values(), startsWith);
            case "status" -> filterEnumValues(NotificationStatus.values(), startsWith);
            case "side" -> filterEnumValues(NotificationSide.values(), startsWith);
            case "severity" -> filterEnumValues(NotificationSeverity.values(), startsWith);
            case "type" -> filterEnumValues(NotificationType.values(), startsWith);
            default -> null;
        };
    }

    private List<String> filterEnumValues(Enum<?>[] values, List<String> startsWith) {
        return Arrays.stream(values)
                .map(Enum::name)
                .filter(s -> startsWith == null || startsWith.isEmpty() ||
                        startsWith.stream().anyMatch(prefix -> StringUtils.startsWithIgnoreCase(s, prefix)))
                .toList();
    }
}
