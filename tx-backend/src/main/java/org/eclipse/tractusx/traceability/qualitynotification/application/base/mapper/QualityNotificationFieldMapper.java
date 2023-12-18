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

package org.eclipse.tractusx.traceability.qualitynotification.application.base.mapper;

import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QualityNotificationFieldMapper extends BaseRequestFieldMapper {

    private static final Map<String, String> SUPPORTED_NOTIFICATION_FILTER_FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("bpn", "bpn"),
            Map.entry("status", "status"),
            Map.entry("description", "description"),
            Map.entry("createdDate", "createdDate"),
            Map.entry("channel", "side"),
            Map.entry("errorMessage", "errorMessage"),
            Map.entry("close", "closeReason"),
            Map.entry("accept", "acceptReason"),
            Map.entry("decline", "declineReason"),
            Map.entry("severity", "notifications_severity"),
            Map.entry("createdBy", "notifications_createdBy"),
            Map.entry("createdByName", "notifications_createdByName"),
            Map.entry("sendTo", "notifications_sendTo"),
            Map.entry("sendToName", "notifications_sendToName"),
            Map.entry("targetDate", "notifications_targetDate"),
            Map.entry("assetId", "assets_id")

    );

    @Override
    protected Map<String, String> getSupportedFields() {
        return SUPPORTED_NOTIFICATION_FILTER_FIELDS;
    }
}
