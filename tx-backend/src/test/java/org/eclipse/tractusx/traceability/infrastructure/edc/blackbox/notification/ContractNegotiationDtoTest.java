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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContractNegotiationDtoTest {

    private static final String contractAgreementId = "contractAgreementId";
    private static final String counterPartyAddress = "counterPartyAddress";
    private static final String errorDetail = "errorDetail";
    private static final String id = "id";
    private static final String protocol = "ids-multipart";
    private static final String state = "state";
    private static final ContractNegotiation.Type type = ContractNegotiation.Type.CONSUMER;

    static ContractNegotiationDto contractNegotiationDto;

    @BeforeAll
    static void beforeAll() {
        contractNegotiationDto = ContractNegotiationDto.Builder.newInstance()
            .contractAgreementId(contractAgreementId)
            .counterPartyAddress(counterPartyAddress)
            .errorDetail(errorDetail)
            .id(id)
            .protocol(protocol)
            .state(state)
            .type(type)
            .build();
    }

    @Test
    void getId() {
        assertEquals(id, contractNegotiationDto.getId());
    }

    @Test
    void getCounterPartyAddress() {
        assertEquals(counterPartyAddress, contractNegotiationDto.getCounterPartyAddress());
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, contractNegotiationDto.getProtocol());
    }

    @Test
    void getType() {
        assertEquals(type, contractNegotiationDto.getType());
    }

    @Test
    void getState() {
        assertEquals(state, contractNegotiationDto.getState());
    }

    @Test
    void getErrorDetail() {
        assertEquals(errorDetail, contractNegotiationDto.getErrorDetail());
    }

    @Test
    void getContractAgreementId() {
        assertEquals(contractAgreementId, contractNegotiationDto.getContractAgreementId());
    }

}
