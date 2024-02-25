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

package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.NotificationMethod;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.NotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.EdcNotificationContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationContractServiceTest {

    @Mock
    TraceabilityProperties traceabilityProperties;
    @Mock
    EdcAssetService edcAssetService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    EdcPolicyDefinitionService edcPolicyDefinitionService;

    @Mock
    EdcContractDefinitionService edcContractDefinitionService;

    private EdcNotificationContractService edcNotificationContractService;
    private CreateNotificationContractRequest request;

    private static final String notificationAssetId = "9";
    private static final String accessPolicyId = "99";
    private static final String contractDefinitionId = "999";

    @BeforeEach
    void setUp() throws JsonProcessingException, CreateEdcAssetException, CreateEdcPolicyDefinitionException, CreateEdcContractDefinitionException {
        String rightOperand = "trace3";
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(edcAssetService.createNotificationAsset(any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getRightOperand()).thenReturn(rightOperand);
//        when(edcNotificationAssetService.createNotificationAsset(notificationMethod, request.notificationType())).thenReturn(notificationAssetId);
        when(edcPolicyDefinitionService.createAccessPolicy(rightOperand)).thenReturn(accessPolicyId);
        when(edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId)).thenReturn(contractDefinitionId);
        edcNotificationContractService = new EdcNotificationContractService(
                edcAssetService, edcPolicyDefinitionService, edcContractDefinitionService, traceabilityProperties
        );
    }

    @Test
    void testHandle() {
        CreateNotificationContractResponse response = edcNotificationContractService.handle(request);
        assertThat(notificationAssetId).isEqualTo(response.notificationAssetId());
        assertThat(accessPolicyId).isEqualTo(response.accessPolicyId());
        assertThat(contractDefinitionId).isEqualTo(response.contractDefinitionId());
    }
}
