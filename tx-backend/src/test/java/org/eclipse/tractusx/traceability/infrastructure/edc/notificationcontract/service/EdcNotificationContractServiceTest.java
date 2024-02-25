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

import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.DeleteEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.DeleteEdcPolicyDefinitionException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationContractServiceTest {

    @Mock
    TraceabilityProperties traceabilityProperties;
    @Mock
    EdcAssetService edcNotificationAssetService;

    @Mock
    EdcPolicyDefinitionService edcPolicyDefinitionService;

    @Mock
    EdcContractDefinitionService edcContractDefinitionService;

    private EdcNotificationContractService edcNotificationContractService;


    private static final String notificationAssetId = "9";
    private static final String accessPolicyId = "99";
    private static final String contractDefinitionId = "999";

    @BeforeEach
    void setUp() {
        edcNotificationContractService = new EdcNotificationContractService(
                edcNotificationAssetService, edcPolicyDefinitionService, edcContractDefinitionService, traceabilityProperties
        );
    }

    @Test
    void testHandle() throws CreateEdcAssetException, CreateEdcPolicyDefinitionException, CreateEdcContractDefinitionException {
        // given
        String rightOperand = "trace3";
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getUrl()).thenReturn("https://test");
        when(traceabilityProperties.getRightOperand()).thenReturn(rightOperand);
        when(edcPolicyDefinitionService.createAccessPolicy(rightOperand)).thenReturn(accessPolicyId);
        when(edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId)).thenReturn(contractDefinitionId);

        // when
        CreateNotificationContractResponse response = edcNotificationContractService.handle(request);

        // then
        assertThat(notificationAssetId).isEqualTo(response.notificationAssetId());
        assertThat(accessPolicyId).isEqualTo(response.accessPolicyId());
        assertThat(contractDefinitionId).isEqualTo(response.contractDefinitionId());
        verify(edcNotificationAssetService).createNotificationAsset(
                "https://test/api/qualitynotifications/resolve",
                "QUALITY_INVESTIGATION RESOLVE",
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationMethod.RESOLVE,
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationType.QUALITY_INVESTIGATION);
    }

    @Test
    void givenService_whenAssetCreationThrowsException_thenThrowException() throws CreateEdcAssetException, CreateEdcPolicyDefinitionException, CreateEdcContractDefinitionException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(traceabilityProperties.getUrl()).thenReturn("https://test");
        doThrow(CreateEdcAssetException.class).when(edcNotificationAssetService).createNotificationAsset(any(), any(), any(), any());

        // when/then
        assertThrows(RuntimeException.class, () -> edcNotificationContractService.handle(request));
    }

    @Test
    void givenService_whenPolicyDefinitionServiceThrowsException_thenThrowException() throws CreateEdcAssetException, CreateEdcPolicyDefinitionException, DeleteEdcAssetException {
        // given
        String rightOperand = "trace3";
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getUrl()).thenReturn("https://test");
        when(traceabilityProperties.getRightOperand()).thenReturn(rightOperand);
        doThrow(CreateEdcPolicyDefinitionException.class).when(edcPolicyDefinitionService).createAccessPolicy(any());

        // when/then
        assertThrows(RuntimeException.class, () -> edcNotificationContractService.handle(request));
        verify(edcNotificationAssetService).deleteAsset(any());
    }

    @Test
    void givenService_whenContractDefinitionServiceThrowsException_thenThrowException() throws CreateEdcAssetException, CreateEdcContractDefinitionException, DeleteEdcAssetException, DeleteEdcPolicyDefinitionException {
        // given
        String rightOperand = "trace3";
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getUrl()).thenReturn("https://test");
        when(traceabilityProperties.getRightOperand()).thenReturn(rightOperand);
        doThrow(CreateEdcContractDefinitionException.class).when(edcContractDefinitionService).createContractDefinition(any(), any());

        // when/then
        assertThrows(RuntimeException.class, () -> edcNotificationContractService.handle(request));
        verify(edcPolicyDefinitionService).deleteAccessPolicy(any());
        verify(edcNotificationAssetService).deleteAsset(any());
    }
}
