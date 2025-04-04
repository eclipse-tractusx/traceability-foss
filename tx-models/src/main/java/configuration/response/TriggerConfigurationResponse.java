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

package configuration.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TriggerConfigurationResponse {
    @Schema(example = "10")
    Long id;
    @Schema(example = "* * * * * *")
    String cronExpressionRegisterOrderTTLReached;
    @Schema(example = "* * * * * *")
    String cronExpressionMapCompletedOrders;
    @Schema(example = "60")
    int partTTL;
    @Schema(example = "60")
    int aasTTL;
    @Schema(example = "* * * * * *")
    private String cronExpressionAASLookup;
    @Schema(example = "* * * * * *")
    private String cronExpressionAASCleanup;

}
