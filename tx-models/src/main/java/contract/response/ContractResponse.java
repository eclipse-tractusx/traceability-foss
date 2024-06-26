/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package contract.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import common.CustomOffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContractResponse {
    @Schema(example = "66", maxLength = 255)
    private String contractId;
    @Schema(example = "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp", maxLength = 255)
    private String counterpartyAddress;
    @Schema(example = "2023-02-21T21:27:10.734950Z", maxLength = 255)
    @JsonSerialize(using = CustomOffsetDateTimeSerializer.class)
    private OffsetDateTime creationDate;
    @Schema(example = "2023-02-21T21:27:10.734950Z", maxLength = 255)
    @JsonSerialize(using = CustomOffsetDateTimeSerializer.class)
    private OffsetDateTime endDate;
    @Schema(example = "FINALIZED", maxLength = 255)
    private String state;
    @Schema(example = "{\\\"@id\\\":\\\"eb0c8486-914a-4d36-84c0-b4971cbc52e4\\\",\\\"@type\\\":\\\"odrl:Set\\\",\\\"odrl:permission\\\":{\\\"odrl:target\\\":\\\"registry-asset\\\",\\\"odrl:action\\\":{\\\"odrl:type\\\":\\\"USE\\\"},\\\"odrl:constraint\\\":{\\\"odrl:or\\\":{\\\"odrl:leftOperand\\\":\\\"PURPOSE\\\",\\\"odrl:operator\\\":{\\\"@id\\\":\\\"odrl:eq\\\"},\\\"odrl:rightOperand\\\":\\\"ID 3.0 Trace\\\"}}},\\\"odrl:prohibition\\\":[],\\\"odrl:obligation\\\":[],\\\"odrl:target\\\":\\\"registry-asset\\\"}", maxLength = 255)
    private String policy;
    private ContractTypeResponse contractType;

}
