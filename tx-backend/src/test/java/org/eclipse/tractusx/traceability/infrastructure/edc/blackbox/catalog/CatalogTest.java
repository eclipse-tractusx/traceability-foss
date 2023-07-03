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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog;

import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CatalogTest {

    private static final String id = "id";
    private static final List<ContractOffer> contractOffers = new ArrayList<>();

    private Catalog catalog;

 /*   @BeforeEach
    void setUp() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_ID, 123);
        properties.put(PROPERTY_NAME, "My Asset");
        properties.put(PROPERTY_DESCRIPTION, "This is a description of my asset.");
        properties.put(PROPERTY_VERSION, 1.0);
        properties.put(PROPERTY_CONTENT_TYPE, "image/jpeg");
        properties.put(PROPERTY_NOTIFICATION_TYPE, "qualityinvestigation");
        properties.put(PROPERTY_NOTIFICATION_METHOD, "update");
        Policy policy = Policy.Builder.newInstance().build();
        Asset asset = Asset.Builder.newInstance().properties(properties).build();
        ContractOffer contractOffer = ContractOffer.Builder.newInstance()
                .policy(policy)
                .asset(asset)
                .id(id)
                .build();
        contractOffers.add(contractOffer);
        catalog = Catalog.Builder.newInstance()
                .id(id)
                .build();
    }

    @Test
    void getId() {
        assertEquals(id, catalog.getId());
    }

    @Test
    void getContractOffers() {
        assertEquals(contractOffers.size(), catalog.getContractOffers().size());
    }*/
}
