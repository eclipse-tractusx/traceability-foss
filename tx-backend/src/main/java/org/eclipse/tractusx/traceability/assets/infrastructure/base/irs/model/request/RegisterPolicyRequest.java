/********************************************************************************
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request;

import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public record RegisterPolicyRequest(
        String policyId,
        Instant validUntil,
        List<Permission> permissions
) {
    public static RegisterPolicyRequest from(String leftOperand, OperatorType operatorType, String rightOperand, OffsetDateTime ttl) {
        return new RegisterPolicyRequest(
                rightOperand,
                ttl.toInstant(),
                List.of(new Permission(
                        PolicyType.USE,
                        new Constraints(
                                List.of(new Constraint(leftOperand, new Operator(operatorType), rightOperand)),
                                List.of(new Constraint(leftOperand, new Operator(operatorType), rightOperand))))
                ));
    }
}
