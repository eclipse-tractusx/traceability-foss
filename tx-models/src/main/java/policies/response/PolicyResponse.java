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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import common.CustomOffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record PolicyResponse(
        @Schema(example = "5a00bb50-0253-405f-b9f1-1a3150b9d51d")
        String policyId,

        @JsonSerialize(using = CustomOffsetDateTimeSerializer.class)
        OffsetDateTime createdOn,
        @JsonSerialize(using = CustomOffsetDateTimeSerializer.class)
        OffsetDateTime validUntil,

        @JsonAlias({"odrl:permission"})
        List<PermissionResponse> permissions,
        String businessPartnerNumber) {

    public static Policy toDomain(PolicyResponse policyResponse){
        if (policyResponse == null) {
            return null;
        }

       return Policy.builder()
                .policyId(policyResponse.policyId)
                .createdOn(policyResponse.createdOn)
                .validUntil(policyResponse.validUntil)
                .permissions(PermissionResponse.toDomain(policyResponse.permissions))
                .build();
    }

}
