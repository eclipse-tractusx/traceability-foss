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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PermissionResponse(
        @Schema(
                implementation = PolicyType.class,
                example = "use"
        )
        @JsonAlias({"odrl:action"})
        PolicyTypeResponse action,

        @Schema
        @JsonAlias({"odrl:constraint"})
        ConstraintsResponse constraint
) {

    public static PolicyType toDomain(PolicyTypeResponse response){
        return switch (response) {
            case ACCESS -> PolicyType.ACCESS;
            case USE -> PolicyType.USE;
        };
    }
    public static List<Permission> toDomain(List<PermissionResponse> permissionResponses) {
        if (permissionResponses == null) {
            return null;
        }
        return permissionResponses.stream()
                .map(PermissionResponse::toDomain)
                .collect(Collectors.toList());
    }
    public static Permission toDomain(PermissionResponse permissionResponse) {
        if (permissionResponse == null) {
            return null;
        }
       return Permission.builder()
                .action(toDomain(permissionResponse.action))
                .constraint(ConstraintsResponse.toDomain(permissionResponse.constraint))
                .build();
    }
}
