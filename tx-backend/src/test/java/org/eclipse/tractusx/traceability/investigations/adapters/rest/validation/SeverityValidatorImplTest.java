/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeverityValidatorImplTest {

    private final SeverityValidatorImpl validator = new SeverityValidatorImpl();

    @Test
    void testSeveritySuccess() {
        boolean isMinor = validator.isValid("MINOR", null);
        boolean isMajor = validator.isValid("MAJOR", null);
        boolean isLifeThreatening = validator.isValid("LIFE-THREATENING", null);
        boolean isCritical = validator.isValid("CRITICAL", null);
        boolean isLifeThreateningUnderscore = validator.isValid("LIFE_THREATENING", null);

        boolean wrongParameter = validator.isValid("anything", null);
        assertTrue(isMinor, "MINOR should pass validation");
        assertTrue(isMajor, "MAJOR should pass validation");
        assertTrue(isLifeThreatening, "LIFE-THREATENING should pass validation");
        assertTrue(isCritical, "CRITICAL should pass validation");
        assertTrue(isLifeThreateningUnderscore, "LIFE_THREATENING should pass validation");
        assertFalse(wrongParameter, "anything should not pass validation");
    }

}
