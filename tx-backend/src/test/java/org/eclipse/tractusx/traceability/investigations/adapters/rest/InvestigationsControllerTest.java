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

package org.eclipse.tractusx.traceability.investigations.adapters.rest;

import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.investigations.adapters.jpa.PersistentInvestigationsRepository;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsPublisherService;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReadService;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvestigationsControllerTest {

    long id = 9L;

    @InjectMocks
    InvestigationsController investigationsController;

    @Mock
    InvestigationsReadService investigationsReadService;

    @Mock
    InvestigationsRepository investigationsRepository;

    @Mock
    PersistentInvestigationsRepository persistentInvestigationsRepository;

    @Mock
    InvestigationsPublisherService investigationsPublisherService;

    @Mock
    TraceabilityProperties traceabilityProperties;

    @Test
    void getInvestigation() {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(
            InvestigationStatus.CREATED, InvestigationStatus.CREATED);
        // when(investigationsReadService.loadInvestigation(investigationId)).thenReturn(investigation);
        investigationsController.getInvestigation(id);
    }

    @Test
    void approveInvestigation() {
        investigationsController.approveInvestigation(id);
    }

    @Test
    void cancelInvestigation() {
        investigationsController.cancelInvestigation(id);
    }

}
