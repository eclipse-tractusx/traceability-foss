/********************************************************************************
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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.edc.client.ContractNegotiationService;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.EndpointDataReferenceStorage;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.irs.edc.client.model.NegotiationResponse;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyCheckerService;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.HttpCallService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.NotificationsEDCFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsEDCFacadeTest {
    @Mock
    HttpCallService httpCallService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    EdcProperties edcProperties;
    @Mock
    EDCCatalogFacade edcCatalogFacade;
    @Mock
    ContractNegotiationService contractNegotiationService;
    @Mock
    EndpointDataReferenceStorage endpointDataReferenceStorage;
    @Mock
    PolicyCheckerService policyCheckerService;
    @Mock
    EndpointDataReference endpointDataReference;
    @InjectMocks
    NotificationsEDCFacade notificationsEDCFacade;

    @Test
    void givenCorrectInvestigationMessage_whenStartEdcTransfer_thenSendIt() throws Exception {
        // given
        final String receiverEdcUrl = "https://receiver.com";
        final String senderEdcUrl = "https://sender.com";
        final String agreementId = "negotiationId";
        final String dataReferenceEndpoint = "https://endpoint.com";
        final QualityNotificationMessage notificationMessage = QualityNotificationMessage.builder()
                .type(QualityNotificationType.INVESTIGATION)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .isInitial(true)
                .build();
        final CatalogItem catalogItem = CatalogItem.builder()
                .build();
        final String idsPath = "/api/v1/dsp";
        when(edcProperties.getIdsPath()).thenReturn(idsPath);
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(catalogItem));
        when(policyCheckerService.isValid(null)).thenReturn(true);
        when(contractNegotiationService.negotiate(receiverEdcUrl + idsPath, catalogItem))
                .thenReturn(NegotiationResponse.builder().contractAgreementId(agreementId).build());
        when(endpointDataReference.getEndpoint()).thenReturn("endpoint");
        when(endpointDataReference.getAuthCode()).thenReturn("authCode");
        when(endpointDataReference.getAuthKey()).thenReturn("authKey");
        when(endpointDataReference.getEndpoint()).thenReturn(dataReferenceEndpoint);
        when(endpointDataReferenceStorage.remove(agreementId)).thenReturn(Optional.ofNullable(endpointDataReference));
        when(objectMapper.writeValueAsString(any())).thenReturn("{body}");

        // when
        notificationsEDCFacade.startEdcTransfer(notificationMessage, receiverEdcUrl, senderEdcUrl);

        // then
        verify(httpCallService).sendRequest(any());
    }

    @Test
    void givenCorrectInvestigationMessageButSendRequestThrowsException_whenStartEdcTransfer_thenThrowSendNotificationException() throws Exception {
        // given
        final String receiverEdcUrl = "https://receiver.com";
        final String senderEdcUrl = "https://sender.com";
        final String agreementId = "negotiationId";
        final String dataReferenceEndpoint = "https://endpoint.com";
        final QualityNotificationMessage notificationMessage = QualityNotificationMessage.builder()
                .type(QualityNotificationType.INVESTIGATION)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .isInitial(true)
                .build();
        final CatalogItem catalogItem = CatalogItem.builder()
                .build();
        final String idsPath = "/api/v1/dsp";
        when(edcProperties.getIdsPath()).thenReturn(idsPath);
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(catalogItem));
        when(policyCheckerService.isValid(null)).thenReturn(true);
        when(contractNegotiationService.negotiate(receiverEdcUrl + idsPath, catalogItem))
                .thenReturn(NegotiationResponse.builder().contractAgreementId(agreementId).build());
        when(endpointDataReference.getEndpoint()).thenReturn("endpoint");
        when(endpointDataReference.getAuthCode()).thenReturn("authCode");
        when(endpointDataReference.getAuthKey()).thenReturn("authKey");
        when(endpointDataReference.getEndpoint()).thenReturn(dataReferenceEndpoint);
        when(endpointDataReferenceStorage.remove(agreementId)).thenReturn(Optional.ofNullable(endpointDataReference));
        when(objectMapper.writeValueAsString(any())).thenReturn("{body}");
        doThrow(new RuntimeException()).when(httpCallService).sendRequest(any());

        // when/then
        assertThrows(SendNotificationException.class, () -> notificationsEDCFacade.startEdcTransfer(notificationMessage, receiverEdcUrl, senderEdcUrl));
    }

    @Test
    void givenCorrectInvestigationMessageButNegotiateContractAgreementHasNoCatalogItem_whenStartEdcTransfer_thenThrowContractNegotiationException() throws Exception {
        // given
        final String receiverEdcUrl = "https://receiver.com";
        final String senderEdcUrl = "https://sender.com";
        final QualityNotificationMessage notificationMessage = QualityNotificationMessage.builder()
                .type(QualityNotificationType.INVESTIGATION)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .isInitial(true)
                .build();
        final CatalogItem catalogItem = CatalogItem.builder()
                .build();
        final String idsPath = "/api/v1/dsp";
        when(edcProperties.getIdsPath()).thenReturn(idsPath);
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(catalogItem));
        when(policyCheckerService.isValid(null)).thenReturn(true);
        when(contractNegotiationService.negotiate(receiverEdcUrl + idsPath, catalogItem))
                .thenReturn(null);

        // when/then
        assertThrows(ContractNegotiationException.class, () -> notificationsEDCFacade.startEdcTransfer(notificationMessage, receiverEdcUrl, senderEdcUrl));
    }

    @Test
    void givenCorrectInvestigationMessageButCatalogItem_whenStartEdcTransfer_thenThrowSendNotificationException() {
        // given
        final String receiverEdcUrl = "https://receiver.com";
        final String senderEdcUrl = "https://sender.com";
        final QualityNotificationMessage notificationMessage = QualityNotificationMessage.builder()
                .type(QualityNotificationType.INVESTIGATION)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .isInitial(true)
                .build();
        final String idsPath = "/api/v1/dsp";
        when(edcProperties.getIdsPath()).thenReturn(idsPath);
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        // when/then
        assertThrows(NoCatalogItemException.class, () -> notificationsEDCFacade.startEdcTransfer(notificationMessage, receiverEdcUrl, senderEdcUrl));
    }
}
