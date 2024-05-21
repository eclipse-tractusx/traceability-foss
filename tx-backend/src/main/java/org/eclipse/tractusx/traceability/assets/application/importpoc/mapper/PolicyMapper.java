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
package org.eclipse.tractusx.traceability.assets.application.importpoc.mapper;

import assets.importpoc.ConstraintResponse;
import assets.importpoc.ConstraintsResponse;
import assets.importpoc.PermissionResponse;
import assets.importpoc.PolicyResponse;
import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.irs.edc.client.asset.model.OdrlContext;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcOperator;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermission;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraint;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraintExpression;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@UtilityClass
public class PolicyMapper {
    public static EdcCreatePolicyDefinitionRequest mapToEdcPolicyRequest(PolicyResponse policy) {
        // TODO https://github.com/eclipse-tractusx/traceability-foss/issues/978 context needs to be updated with the required ones
        OdrlContext odrlContext = OdrlContext.builder().odrl("http://www.w3.org/ns/odrl/2/").build();
        EdcPolicy edcPolicy = EdcPolicy.builder().odrlPermissions(mapToPermissions(policy.permissions())).type("odrl:Set").build();
        return EdcCreatePolicyDefinitionRequest.builder()
                .policyDefinitionId(policy.policyId())
                .policy(edcPolicy)
                .odrlContext(odrlContext)
                .type("odrl:Set")
                .build();
    }

    private static List<EdcPolicyPermission> mapToPermissions(List<PermissionResponse> permissions) {
        return permissions.stream().map(permission -> EdcPolicyPermission.builder()
                // TODO https://github.com/eclipse-tractusx/traceability-foss/issues/978 here we need to make sure that the USE is lowercase in the object.
                .action(permission.action().name())
                .edcPolicyPermissionConstraints(mapToConstraint(permission.constraints()))
                .build()
        ).toList();
    }

    private static EdcPolicyPermissionConstraint mapToConstraint(ConstraintsResponse constraintsResponse) {
        return EdcPolicyPermissionConstraint.builder()
                .type("AtomicConstraint")
                .orExpressions(mapToConstraintExpression(constraintsResponse.or()))
                .andExpressions(mapToConstraintExpression(constraintsResponse.and()))
                .build();
    }

    private static List<EdcPolicyPermissionConstraintExpression> mapToConstraintExpression(List<ConstraintResponse> constraints) {
        return emptyIfNull(constraints).stream().map(constraint -> EdcPolicyPermissionConstraintExpression.builder()
                        .type("Constraint")
                        .leftOperand(constraint.leftOperand())
                        .rightOperand(constraint.rightOperand())
                        .operator(EdcOperator.builder()
                                .operatorId("odrl:" + constraint.operatorTypeResponse().getCode())
                                .build())
                        .build())
                .toList();
    }
}
