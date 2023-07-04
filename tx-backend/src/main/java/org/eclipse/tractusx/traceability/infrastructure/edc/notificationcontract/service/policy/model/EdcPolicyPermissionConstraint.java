/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdcPolicyPermissionConstraint {

    // todo odrl:

    @JsonProperty("edctype")
    private final String edcType;

    @JsonProperty("leftExpression")
    private final EdcPolicyPermissionConstraintExpression leftExpression;

    @JsonProperty("rightExpression")
    private final EdcPolicyPermissionConstraintExpression rightExpression;

    @JsonProperty("operator")
    private final String operator;

    public EdcPolicyPermissionConstraint(String edcType,
                                         EdcPolicyPermissionConstraintExpression leftExpression,
                                         EdcPolicyPermissionConstraintExpression rightExpression,
                                         String operator) {
        this.edcType = edcType;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.operator = operator;
    }

    public String getEdcType() {
        return edcType;
    }

    public EdcPolicyPermissionConstraintExpression getLeftExpression() {
        return leftExpression;
    }

    public EdcPolicyPermissionConstraintExpression getRightExpression() {
        return rightExpression;
    }

    public String getOperator() {
        return operator;
    }
}
