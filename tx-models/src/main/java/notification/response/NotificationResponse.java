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
package notification.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@ArraySchema(arraySchema = @Schema(description = "Notifications", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
public class NotificationResponse {

    @Schema(example = "66", maxLength = 255)
    @Size(max = 255)
    private Long id;

    @Schema(example = "Title", maxLength = 255)
    @Size(max = 255)
    private String title;

    @Schema(example = "CREATED", maxLength = 255)
    @Size(max = 255)
    private NotificationStatusResponse status;

    @Schema(example = "DescriptionText", maxLength = 1000)
    @Size(max = 1000)
    private String description;

    @Schema(example = "BPNL00000003AYRE", maxLength = 255)
    @Size(max = 255)
    private String createdBy;

    @Schema(example = "Tier C", maxLength = 255)
    @Size(max = 255)
    private String createdByName;

    @Schema(example = "2023-02-21T21:27:10.734950Z", maxLength = 50)
    @Size(max = 50)
    private String createdDate;

    @Schema(example = "2023-02-21T21:27:10.734950Z", maxLength = 50)
    @Size(max = 50)
    private String updatedDate;

    @Size(max = 1000)
    @Schema(name = "assetIds", type = "array", example = "[\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd\",\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70529fcbd\",\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70530fcbd\"]")
    private List<String> assetIds;

    @Schema(example = "SENDER", maxLength = 255)
    @Size(max = 255)
    private NotificationSideResponse channel;

    @Schema(example = "BPNL00000003AYRE", maxLength = 255)
    @Size(max = 255)
    private String sendTo;

    @Schema(example = "Tier C", maxLength = 255)
    @Size(max = 255)
    private String sendToName;

    @Schema(example = "MINOR", maxLength = 255)
    @Size(max = 255)
    private NotificationSeverityResponse severity;

    @Schema(example = "ALERT", maxLength = 50)
    @Size(max = 50)
    private NotificationTypeResponse type;

    @Schema(example = "2099-02-21T21:27:10.734950Z", maxLength = 50)
    @Size(max = 50)
    private String targetDate;

    private List<NotificationMessageResponse> messages;


    public Optional<NotificationMessageResponse> latestNotification() {
        return messages.stream().max(Comparator.comparing(NotificationMessageResponse::getMessageDate)).stream().findFirst();
    }

}
