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

package qualitynotification.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartQualityNotificationRequest {

    @Schema(example = "[\"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978\"]", minLength = 1, maxLength = 100, description = "Specify at least 1 and at most 100 partIds")
    private List<String> partIds;

    @Schema(example = "The description", minLength = 15, maxLength = 1000, description = "Description should have at least 15 characters and at most 1000 characters")
    private String description;

    @Schema(example = "2099-03-11T22:44:06.333826952Z")
    @Future(message = "Specify at least the current day or a date in future")
    private Instant targetDate;

    @NotNull
    @Schema(example = "MINOR")
    private QualityNotificationSeverityRequest severity;

    @Schema(example = "true")
    private boolean isAsBuilt = true;

    @Schema(example = "BPN00001123123AS")
    private String receiverBpn;

}
