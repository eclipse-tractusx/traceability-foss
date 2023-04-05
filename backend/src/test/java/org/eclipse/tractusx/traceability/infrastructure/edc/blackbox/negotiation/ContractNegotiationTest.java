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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.agreement.ContractAgreement;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContractNegotiationTest {

    private static final ContractNegotiation.Type type = ContractNegotiation.Type.CONSUMER;
    private static final String id = "id";
    private static final String counterPartyId = "counterPartyId";
    private static final String counterPartyAddress = "counterPartyAddress";
    private static final String correlationId = "correlationId";
    private static final String protocol = "protocol";
    private static final int state = 9;
    private static final int stateCount = 99;
    private static final long stateTimestamp = 999L;
    private static final String errorDetail = "errorDetail";
    private static final String providerAgentId = "providerAgentId";
    private static final String consumerAgentId = "consumerAgentId";
    private static final String assetId = "assetId";

    static ContractOffer contractOffer;
    static List<ContractOffer> contractOffers;
    static ContractNegotiation contractNegotiation;
    static Map<String, String> traceContext;
    static ContractAgreement contractAgreement;

    @BeforeAll
    static void beforeAll() {
        Policy policy = Policy.Builder.newInstance().build();
        contractOffer = ContractOffer.Builder.newInstance()
                .id(id)
                .policy(policy)
                .build();
        contractOffers = new ArrayList<>();
        contractOffers.add(contractOffer);
        traceContext = new HashMap<>();
        traceContext.put("key0", "value");
        contractAgreement = ContractAgreement.Builder.newInstance()
                .id(id)
                .providerAgentId(providerAgentId)
                .consumerAgentId(consumerAgentId)
                .policy(policy)
                .assetId(assetId)
                .build();
        contractNegotiation = ContractNegotiation.Builder.newInstance()
            .type(type)
            .id(id)
            .counterPartyId(counterPartyId)
            .counterPartyAddress(counterPartyAddress)
            .correlationId(correlationId)
            .protocol(protocol)
                .state(state)
                .stateCount(stateCount)
                .stateTimestamp(stateTimestamp)
                .contractOffers(contractOffers)
                .traceContext(traceContext)
                .contractAgreement(contractAgreement)
                .errorDetail(errorDetail)
            .build();
    }

    @Test
    void getType() {
        assertEquals(type.name(), contractNegotiation.getType().name());
    }

    @Test
    void getId() {
        assertEquals(id, contractNegotiation.getId());
    }

    @Test
    void getCounterPartyId() {
        assertEquals(counterPartyId, contractNegotiation.getCounterPartyId());
    }

    @Test
    void getCounterPartyAddress() {
        assertEquals(counterPartyAddress, contractNegotiation.getCounterPartyAddress());
    }

    @Test
    void getCorrelationId() {
        assertEquals(correlationId, contractNegotiation.getCorrelationId());
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, contractNegotiation.getProtocol());
    }

    @Test
    void getState() {
        assertEquals(state, contractNegotiation.getState());
    }

    @Test
    void getStateCount() {
        assertEquals(stateCount, contractNegotiation.getStateCount());
    }

    @Test
    void getStateTimestamp() {
        assertEquals(stateTimestamp, contractNegotiation.getStateTimestamp());
    }

    @Test
    void getContractOffers() {
        assertEquals(contractOffers.size(), contractNegotiation.getContractOffers().size());
    }

    @Test
    void getErrorDetail() {
        assertEquals(errorDetail, contractNegotiation.getErrorDetail());
    }

    @Test
    void getTraceContext() {
        assertEquals(traceContext, contractNegotiation.getTraceContext());
    }

    @Test
    void getLastContractOffer() {
        assertEquals(contractOffer, contractNegotiation.getLastContractOffer());
    }

    @Test
    void getContractAgreement() {
        assertEquals(contractAgreement, contractNegotiation.getContractAgreement());
    }

}
