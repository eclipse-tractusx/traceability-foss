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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;

import java.util.List;

@Builder
public record ConstraintsResponse(
        @ArraySchema
        @JsonAlias({"odrl:and"})
        List<ConstraintResponse> and,
        @ArraySchema
        @JsonAlias({"odrl:or"})
        List<ConstraintResponse> or
) {
    public static Constraints toDomain(ConstraintsResponse constraintsResponse){
        if (constraintsResponse == null) {
            return null;
        }
       return Constraints.builder()
                .and(ConstraintResponse.toDomain(constraintsResponse.and))
                .or(ConstraintResponse.toDomain(constraintsResponse.or))
                .build();
    }
}
