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

import java.util.HashMap;
import java.util.Optional;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.Asset;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.DeleteEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.GetEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.UpdateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.irs.edc.client.model.EdcTechnicalServiceAuthentication;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.DeleteEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationMethod;
import org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService.BASE_URL_PROPERTY;
import static org.eclipse.tractusx.traceability.testdata.PolicyTestDataFactory.createIrsPolicyResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationContractServiceTest {

    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Mock
    private EdcAssetService edcNotificationAssetService;

    @Mock
    private EdcPolicyDefinitionService edcPolicyDefinitionService;

    @Mock
    private EdcContractDefinitionService edcContractDefinitionService;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private EdcProperties edcProperties;

    @Mock
    private EDCCatalogFacade edcCatalogFacade;

    @InjectMocks
    private EdcNotificationContractService edcNotificationContractService;

    private static final String notificationAssetId = "9";
    private static final String accessPolicyId = "99";
    private static final String contractDefinitionId = "999";

    @BeforeEach
    void setUp() {
        when(traceabilityProperties.getBpn()).thenReturn(new BPN("bpn"));
    }

    @Test
    void shouldCreateNotificationContract_createNewAsset() throws CreateEdcAssetException, CreateEdcPolicyDefinitionException, CreateEdcContractDefinitionException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        final EdcTechnicalServiceAuthentication edcTechnicalServiceAuthentication =
                EdcTechnicalServiceAuthentication.builder().technicalServiceApiKey(traceabilityProperties.getTechnicalServiceApiKey()).build();

        // Create a single IrsPolicyResponse and put it into a map
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse("test", OffsetDateTime.now(), "orLeft", "andLeft", "or", "and");
        Map<String, List<IrsPolicyResponse>> policyMap = Map.of("testKey", List.of(irsPolicyResponse));

        List<PolicyResponse> policyResponses = IrsPolicyResponse.toResponse(policyMap);
        when(policyRepository.getLatestPoliciesByApplicationBPNOrDefaultPolicy()).thenReturn(List.of((policyResponses.get(0))));

        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);

        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getInternalUrl()).thenReturn("https://test");
        when(edcPolicyDefinitionService.createAccessPolicy(any(EdcCreatePolicyDefinitionRequest.class))).thenReturn(accessPolicyId);
        when(edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId)).thenReturn(contractDefinitionId);

        // when
        List<CreateNotificationContractResponse> response = edcNotificationContractService.createNotificationContract(request);

        // then
        assertThat(notificationAssetId).isEqualTo(response.get(0).notificationAssetId());
        assertThat(accessPolicyId).isEqualTo(response.get(0).accessPolicyId());
        assertThat(contractDefinitionId).isEqualTo(response.get(0).contractDefinitionId());
        verify(edcNotificationAssetService).createNotificationAsset(
                "https://test/api/internal/qualitynotifications/resolve",
                "QUALITY_INVESTIGATION RESOLVE",
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationMethod.RESOLVE,
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationType.QUALITY_INVESTIGATION,
                edcTechnicalServiceAuthentication);
    }

    @Test
    void shouldNotCreateNotificationContract_alreadyExists_sameBaseUrl() throws CreateEdcAssetException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        final EdcTechnicalServiceAuthentication edcTechnicalServiceAuthentication =
                EdcTechnicalServiceAuthentication.builder().technicalServiceApiKey(traceabilityProperties.getTechnicalServiceApiKey()).build();

        final String policyId = "test";

        // Create a single IrsPolicyResponse and put it into a map
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse(policyId, OffsetDateTime.now(), "orLeft", "andLeft", "or", "and");
        Map<String, List<IrsPolicyResponse>> policyMap = Map.of("testKey", List.of(irsPolicyResponse));

        List<PolicyResponse> policyResponses = IrsPolicyResponse.toResponse(policyMap);
        when(policyRepository.getLatestPoliciesByApplicationBPNOrDefaultPolicy()).thenReturn(List.of((policyResponses.get(0))));

        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);

        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(
                CatalogItem.builder()
                        .itemId("qualityinvestigationnotification-resolve")
                        .assetPropId("qualityinvestigationnotification-resolve")
                        .build()));

        // when
        List<CreateNotificationContractResponse> response = edcNotificationContractService.createNotificationContract(request);

        // then
        assertThat(response).isEmpty();
        verify(edcNotificationAssetService, never()).createNotificationAsset(
                "https://test/api/internal/qualitynotifications/resolve",
                "QUALITY_INVESTIGATION RESOLVE",
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationMethod.RESOLVE,
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationType.QUALITY_INVESTIGATION,
                edcTechnicalServiceAuthentication);
    }

    @Test
    void shouldNotCreateNotificationContract_alreadyExists_updateBaseUrl()
            throws CreateEdcAssetException, GetEdcAssetException, UpdateEdcAssetException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        final EdcTechnicalServiceAuthentication edcTechnicalServiceAuthentication =
                EdcTechnicalServiceAuthentication.builder().technicalServiceApiKey(traceabilityProperties.getTechnicalServiceApiKey()).build();

        final String policyId = "test";

        // Create a single IrsPolicyResponse and put it into a map
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse(policyId, OffsetDateTime.now(), "orLeft", "andLeft", "or", "and");
        Map<String, List<IrsPolicyResponse>> policyMap = Map.of("testKey", List.of(irsPolicyResponse));

        List<PolicyResponse> policyResponses = IrsPolicyResponse.toResponse(policyMap);
        when(policyRepository.getLatestPoliciesByApplicationBPNOrDefaultPolicy()).thenReturn(List.of((policyResponses.get(0))));

        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);

        when(traceabilityProperties.getInternalUrl()).thenReturn("http://localhost");

        final String assetId = "qualityinvestigationnotification-resolve";

        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(
                CatalogItem.builder()
                        .itemId(assetId)
                        .assetPropId(assetId)
                        .build()));

        Map<String, Object> dataAddressProperties = new HashMap<>();
        dataAddressProperties.put(BASE_URL_PROPERTY, "/old/url");
        Asset existingAsset = Asset.builder().dataAddress(dataAddressProperties).build();

        when(edcNotificationAssetService.getAsset(eq(assetId))).thenReturn(ResponseEntity.of(Optional.of(existingAsset)));

        // when
        List<CreateNotificationContractResponse> response = edcNotificationContractService.createNotificationContract(request);

        // then
        assertThat(response).isEmpty();
        verify(edcNotificationAssetService, never()).createNotificationAsset(
                "https://test/api/internal/qualitynotifications/resolve",
                "QUALITY_INVESTIGATION RESOLVE",
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationMethod.RESOLVE,
                org.eclipse.tractusx.irs.edc.client.asset.model.NotificationType.QUALITY_INVESTIGATION,
                edcTechnicalServiceAuthentication);

        verify(edcNotificationAssetService).updateAsset(existingAsset);
        assertThat(existingAsset.getDataAddress().get(BASE_URL_PROPERTY))
                .isEqualTo("http://localhost/api/internal/qualitynotifications/resolve");
    }

    @Test
    void givenService_whenAssetCreationThrowsException_thenThrowException() throws CreateEdcAssetException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;
        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);
        when(traceabilityProperties.getInternalUrl()).thenReturn("https://test");
        doThrow(CreateEdcAssetException.class).when(edcNotificationAssetService).createNotificationAsset(any(), any(), any(), any(), any());

        // when/then
        assertThrows(CreateNotificationContractException.class, () -> edcNotificationContractService.createNotificationContract(request));
    }

    @Test
    void givenService_whenPolicyDefinitionServiceThrowsException_thenThrowException() throws CreateEdcAssetException, CreateEdcPolicyDefinitionException, DeleteEdcAssetException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;

        // Create a single IrsPolicyResponse and put it into a map
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse("test", OffsetDateTime.now(), "orLeft", "andLeft", "or", "and");
        Map<String, List<IrsPolicyResponse>> policyMap = Map.of("testKey", List.of(irsPolicyResponse));

        List<PolicyResponse> policyResponses = IrsPolicyResponse.toResponse(policyMap);
        when(policyRepository.getLatestPoliciesByApplicationBPNOrDefaultPolicy()).thenReturn(List.of((policyResponses.get(0))));

        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);

        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getInternalUrl()).thenReturn("https://test");

        doThrow(CreateEdcPolicyDefinitionException.class).when(edcPolicyDefinitionService).createAccessPolicy(any(EdcCreatePolicyDefinitionRequest.class));

        // when/then
        assertThrows(CreateNotificationContractException.class, () -> edcNotificationContractService.createNotificationContract(request));
        verify(edcNotificationAssetService).deleteAsset(any());
    }

    @Test
    void givenService_whenContractDefinitionServiceThrowsException_thenThrowException() throws CreateEdcAssetException, CreateEdcContractDefinitionException, DeleteEdcAssetException, DeleteEdcPolicyDefinitionException {
        // given
        NotificationType notificationType = NotificationType.QUALITY_INVESTIGATION;
        NotificationMethod notificationMethod = NotificationMethod.RESOLVE;

        // Create a single IrsPolicyResponse and put it into a map
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse("test", OffsetDateTime.now(), "orLeft", "andLeft", "or", "and");
        Map<String, List<IrsPolicyResponse>> policyMap = Map.of("testKey", List.of(irsPolicyResponse));

        List<PolicyResponse> policyResponses = IrsPolicyResponse.toResponse(policyMap);
        when(policyRepository.getLatestPoliciesByApplicationBPNOrDefaultPolicy()).thenReturn(List.of((policyResponses.get(0))));

        CreateNotificationContractRequest request = new CreateNotificationContractRequest(notificationType, notificationMethod);

        when(edcNotificationAssetService.createNotificationAsset(any(), any(), any(), any(), any())).thenReturn(notificationAssetId);
        when(traceabilityProperties.getInternalUrl()).thenReturn("https://test");

        doThrow(CreateEdcContractDefinitionException.class).when(edcContractDefinitionService).createContractDefinition(any(), any());

        // when/then
        assertThrows(CreateNotificationContractException.class, () -> edcNotificationContractService.createNotificationContract(request));
        verify(edcPolicyDefinitionService).deleteAccessPolicy(any());
        verify(edcNotificationAssetService).deleteAsset(any());
    }

}
