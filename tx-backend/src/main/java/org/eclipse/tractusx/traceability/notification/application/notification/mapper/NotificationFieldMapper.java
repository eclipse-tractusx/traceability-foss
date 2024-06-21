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

package org.eclipse.tractusx.traceability.notification.application.notification.mapper;

import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFieldMapper extends BaseRequestFieldMapper {

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
            Map.entry("severity", "severity"),
            Map.entry("createdBy", "messages_createdBy"),
            Map.entry("createdByName", "messages_createdByName"),
            Map.entry("sendTo", "messages_sendTo"),
            Map.entry("sendToName", "messages_sendToName"),
            Map.entry("targetDate", "targetDate"),
            Map.entry("assetId", "assets_id"),
            Map.entry("title", "title"),
            Map.entry("type", "type")

    );

    @Override
    protected Map<String, String> getSupportedFields() {
        return SUPPORTED_NOTIFICATION_FILTER_FIELDS;
    }
}
