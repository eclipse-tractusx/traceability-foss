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
package policies.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import policies.response.OperatorTypeResponse;

import java.util.Arrays;
@Builder
@Schema
public record OperatorResponse(
        @JsonProperty("@id")
        @Schema(
                implementation = OperatorType.class,
                example = "odrl:eq"
        )
        OperatorTypeResponse operatorType
) {
    public static OperatorType toDomain(OperatorTypeResponse operatorTypeResponse) {
        if (operatorTypeResponse == null) {
            return null;
        }

        return Arrays.stream(OperatorType.values())
                .filter(type -> type.getCode().equals(operatorTypeResponse.code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + operatorTypeResponse.code));
    }
}
