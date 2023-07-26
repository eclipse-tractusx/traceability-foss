/********************************************************************************
 * Copyright (c) 2021,2022,2023
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 *       2022,2023: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022,2023: BOSCH AG
 * Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators;

import lombok.Builder;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;

/**
 * Check and validate AtomicConstraint as a part of Policy in Catalog fetch from EDC providers.
 */
@Builder
public class AtomicConstraintValidator {

    private AtomicConstraint atomicConstraint;
    private String leftExpressionValue;
    private String rightExpressionValue;
    private Operator expectedOperator;

    public boolean isValid() {
        return isExpressionValid(atomicConstraint.getLeftExpression(),
                leftExpressionValue) && isExpressionValid(
                atomicConstraint.getRightExpression(), rightExpressionValue)
                && expectedOperator.equals(atomicConstraint.getOperator());
    }

    private boolean isExpressionValid(final Expression expression, final String value) {
        if (expression instanceof LiteralExpression literalExpression) {
            return literalExpression.asString().equals(value);
        }
        return false;
    }


}
