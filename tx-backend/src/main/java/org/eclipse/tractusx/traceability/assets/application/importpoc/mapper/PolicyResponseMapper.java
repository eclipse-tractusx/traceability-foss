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
import assets.importpoc.OperatorTypeResponse;
import assets.importpoc.PermissionResponse;
import assets.importpoc.PolicyResponse;
import assets.importpoc.PolicyTypeResponse;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PolicyResponseMapper {

    public static List<PolicyResponse> fromList(List<Policy> allPolicies) {
        return allPolicies
                .stream()
                .map(PolicyResponseMapper::from)
                .collect(Collectors.toList());
    }

    public static PolicyResponse from(final Policy policy) {
        if (policy == null) {
            return null;
        }

        List<PermissionResponse> list = policy.getPermissions()
                .stream()
                .map(PolicyResponseMapper::from)
                .collect(Collectors.toList());

        return new PolicyResponse(
                policy.getPolicyId(),
                policy.getCreatedOn(),
                policy.getValidUntil(),
                list
        );
    }

    public static PermissionResponse from(final Permission permission) {
        List<Constraint> or = permission.getConstraint().getOr();
        List<Constraint> and = permission.getConstraint().getAnd();
        List<ConstraintResponse> andResponse = and.stream()
                .map(constraint -> new ConstraintResponse(constraint.getLeftOperand(), OperatorTypeResponse.valueOf(constraint.getOperator().getOperatorType().name()), List.of(constraint.getRightOperand()))
                ).toList();

        List<ConstraintResponse> orResponse = or.stream()
                .map(constraint -> new ConstraintResponse(constraint.getLeftOperand(), OperatorTypeResponse.valueOf(constraint.getOperator().getOperatorType().name()), List.of(constraint.getRightOperand()))
                ).toList();

        ConstraintsResponse constraintsResponse = new ConstraintsResponse(andResponse, orResponse);

        PolicyTypeResponse policyTypeResponse = Optional.ofNullable(permission.getAction())
                .map(PolicyResponseMapper::from)
                .orElse(null);

        return new PermissionResponse(policyTypeResponse, List.of(constraintsResponse));
    }

    public static PolicyTypeResponse from(final PolicyType policyType) {
        return PolicyTypeResponse.valueOf(policyType.name());
    }

    public static ConstraintResponse from(final Constraint constraint) {
        return new ConstraintResponse(
                constraint.getLeftOperand(),
                from(constraint.getOperator().getOperatorType()),
                List.of(constraint.getRightOperand())
        );
    }

    public static OperatorTypeResponse from(final OperatorType operatorType) {
        return OperatorTypeResponse.valueOf(operatorType.name());
    }


}
