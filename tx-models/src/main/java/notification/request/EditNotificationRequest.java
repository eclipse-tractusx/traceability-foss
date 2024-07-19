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

package notification.request;

import bpn.request.ValidBPN;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EditNotificationRequest {

    @Size(min = 1, max = 255, message = "Specify at least 1 and at most 255 characters for the title")
    @Schema(example = "title", minLength = 1, maxLength = 255)
    private String title;

    @NotNull(message = "Field: receiverBpn must not be null.")
    @ValidBPN
    @Schema(example = "BPNL000000000DWF")
    private String receiverBpn;

    @NotNull(message = "Field: severity must not be null.")
    private NotificationSeverityRequest severity;

    @Schema(example = "2099-03-11T22:44:06.333826952Z")
    @Future(message = "Specify at least the current day or a date in future")
    private Instant targetDate;

    @Schema(example = "The description", minLength = 15, maxLength = 1000)
    @NotNull(message = "Field: description must not be null.")
    @Size(min = 15, max = 1000, message = "Description should have at least 15 characters and at most 1000 characters")
    private String description;

    @Size(min = 1, max = 50, message = "Specify at least 1 and at most 50 assetIds")
    @Schema(example = "[\"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978\"]", minLength = 1, maxLength = 50)
    @NotNull(message = "Field: affectedPartIds must not be null.")
    private List<String> affectedPartIds;
}



