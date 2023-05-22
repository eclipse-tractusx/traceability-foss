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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.agreement;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;
import org.eclipse.tractusx.traceability.infrastructure.test.support.ContractAgreementMother;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContractAgreementTest {

    @Test
    void givenContractAgreement_shouldHaveProperValues() {
        // given
        final Policy policy = ContractAgreementMother.getPolicy();
        final ContractAgreement contractAgreement = ContractAgreementMother.getContractAgreement(policy);

        // then
        assertThat(contractAgreement)
                .hasFieldOrPropertyWithValue("id", "id")
                .hasFieldOrPropertyWithValue("providerAgentId","providerAgentId")
                .hasFieldOrPropertyWithValue("consumerAgentId","consumerAgentId")
                .hasFieldOrPropertyWithValue("contractSigningDate",1L)
                .hasFieldOrPropertyWithValue("contractStartDate", 2L)
                .hasFieldOrPropertyWithValue("contractEndDate", 3L)
                .hasFieldOrPropertyWithValue("assetId", "assetId")
                .hasFieldOrPropertyWithValue("policy", policy);
    }
}
