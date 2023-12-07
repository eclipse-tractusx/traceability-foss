/*********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.ess.domain.service;

import ess.request.EssRequest;
import ess.response.EssResponse;

import java.net.UnknownHostException;
import java.util.List;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.ess.application.service.EssService;
import org.eclipse.tractusx.traceability.ess.domain.model.EssEntity;
import org.eclipse.tractusx.traceability.testdata.EssTestDataFactory;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EssServiceTest {

    @InjectMocks
    private EssService essService;

    @Test
    public void createEssInvestigations() throws UnknownHostException {
        EssRequest essRequest = EssTestDataFactory.createEssRequest();
        EssEntity ess1 = EssTestDataFactory.createEssInvestigation();
        List<EssResponse> response = EssTestDataFactory.createEssResponse(ess1);
        when(essService.createEss(essRequest)).thenReturn(response);
    }

    @Test
    public void getAllEss() {
        EssEntity ess1 = EssTestDataFactory.createEssInvestigation();
        when(essService.getAllEss(any(Pageable.class), SearchCriteria.builder().build()))
            .thenReturn(new PageResult<>(List.of(ess1)));
    }

}
