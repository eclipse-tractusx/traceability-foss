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

package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EdcContractDefinitionCriteriaTest {

    private static final String LEFT = "abc";
    private static final String OP_EQUALS = "=";
    private static final String RIGHT = "xyz";

    private EdcContractDefinitionCriteria edcContractDefinitionCriteria;

    @BeforeEach
    void setUp() {
        edcContractDefinitionCriteria = new EdcContractDefinitionCriteria(
                EdcContractDefinitionCriteriaTest.LEFT,
                EdcContractDefinitionCriteriaTest.OP_EQUALS,
                EdcContractDefinitionCriteriaTest.RIGHT
        );
    }

    @Test
    void getOperandLeft() {
        assertEquals(EdcContractDefinitionCriteriaTest.LEFT, edcContractDefinitionCriteria.getOperandLeft());
    }

    @Test
    void getOperator() {
        assertEquals(EdcContractDefinitionCriteriaTest.OP_EQUALS, edcContractDefinitionCriteria.getOperator());
    }

    @Test
    void getOperandRight() {
        assertEquals(EdcContractDefinitionCriteriaTest.RIGHT, edcContractDefinitionCriteria.getOperandRight());
    }
}
