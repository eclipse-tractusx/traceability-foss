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

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.ContractOfferDescription;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContractOfferDescriptionTest {

    private static final String offerId = "offerId";
    private static final String assetId = "assetId";
    private static final String policyId = "policyId";

    static ContractOfferDescription contractOfferDescription;
/*
    @BeforeAll
    static void beforeAll() {
        Prohibition prohibition = Prohibition.Builder.newInstance().assignee("ME").build();
        Policy policy = Policy.Builder.newInstance()
            .assignee("ME")
            .prohibition(prohibition)
            .build();
        contractOfferDescription = new ContractOfferDescription(
            offerId, assetId, policyId, policy
        );
    }

    @Test
    void getOfferId() {
        assertEquals("offerId", contractOfferDescription.getOfferId());
    }

    @Test
    void getAssetId() {
        assertEquals("assetId", contractOfferDescription.getAssetId());
    }

    @Test
    void getPolicyId() {
        assertEquals("policyId", contractOfferDescription.getPolicyId());
    }

    @Test
    void getPolicy() {
        assertEquals("ME", contractOfferDescription.getPolicy().getAssignee());
        assertEquals("ME", contractOfferDescription.getPolicy().getProhibitions().get(0).getAssignee());
    }*/
}
