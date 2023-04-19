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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset.AssetSelectorExpression;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.query.Criterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ContractDefinitionTest {

    private static final String id = "id";
    private static final String accessPolicyId = "accessPolicyId";
    private static final String contractPolicyId = "contractPolicyId";

    private AssetSelectorExpression selectorExpression;
    private ContractDefinition contractDefinition;

    @BeforeEach
    void setUp() {
        List<Criterion> criteria = new ArrayList<>();
        selectorExpression = AssetSelectorExpression.Builder.newInstance()
            .criteria(criteria)
            .build();
        contractDefinition = ContractDefinition.Builder.newInstance()
            .id(id)
            .accessPolicyId(accessPolicyId)
            .contractPolicyId(contractPolicyId)
            .selectorExpression(selectorExpression)
            .build();
    }

    @Test
    void getId() {
        assertEquals(id, contractDefinition.getId());
    }

    @Test
    void getAccessPolicyId() {
        assertEquals(accessPolicyId, contractDefinition.getAccessPolicyId());
    }

    @Test
    void getContractPolicyId() {
        assertEquals(contractPolicyId, contractDefinition.getContractPolicyId());
    }

    @Test
    void getSelectorExpression() {
        assertEquals(selectorExpression, contractDefinition.getSelectorExpression());
    }

}
