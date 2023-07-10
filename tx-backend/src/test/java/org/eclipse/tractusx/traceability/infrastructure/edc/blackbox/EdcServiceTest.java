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

import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.DataService;
import org.eclipse.edc.catalog.spi.Dataset;
import org.eclipse.edc.catalog.spi.Distribution;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.jsontransformer.EdcTransformer;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.Request;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdcServiceTest {

    @Mock
    private HttpCallService httpCallService;

    @InjectMocks
    private EdcService edcService;

    @Mock
    private EdcProperties edcProperties;
    @Mock
    private EdcTransformerTraceX edcTransformer;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Test
    void testGetCatalog() throws IOException {
        // Arrange
        String consumerEdcDataManagementUrl = "https://example.com/consumer-edc";
        String providerConnectorControlPlaneIDSUrl = "https://example.com/provider-connector";
        Map<String, String> header = Collections.singletonMap("Authorization", "Bearer token");

        Policy policy = Policy.Builder.newInstance().build();

        DataService dataService = DataService.Builder.newInstance()
                .build();
        Distribution distribution = Distribution.Builder.newInstance().format("format")
                .dataService(dataService).build();

        Dataset dataset = Dataset.Builder.newInstance().offer("123", policy).distribution(distribution).build();

        Catalog catalog = Catalog.Builder.newInstance().dataset(dataset).build();

        when(httpCallService.getCatalogForNotification(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header))
                .thenReturn(catalog);

        // Act
        Catalog catalogResult = edcService.getCatalog(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header);

        assertThat(catalogResult).isNotNull();
        // Assert
        verify(httpCallService, times(1))
                .getCatalogForNotification(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header);

    }

    @Test
    void testCatalogThrowBadRequestExceptionWhenDatasetIsEmpty() throws IOException {
        //GIVEN
        String consumerEdcDataManagementUrl = "https://example.com/consumer-edc";
        String providerConnectorControlPlaneIDSUrl = "https://example.com/provider-connector";
        Map<String, String> header = Collections.singletonMap("Authorization", "Bearer token");

        Catalog emptyCatalog = Catalog.Builder.newInstance().datasets(Collections.<Dataset>emptyList()).build();
        when(httpCallService.getCatalogForNotification(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header))
                .thenReturn(emptyCatalog);

        //WHEN
        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> edcService.getCatalog(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header)
        );
        //THEN
        verify(httpCallService).getCatalogForNotification(any(), any(), any());
        assertEquals("Provider has no contract offers for us. Catalog is empty.", badRequestException.getMessage());
    }

    @Test
    void testInitializeContractNegotiation() throws IOException, InterruptedException {
        //GIVEN
        String providerConnectorUrl = "https://example.com/provider-connector";
        String consumerEdcUrl = "https://example.com/consumer-edc";
        Map<String, String> header = Collections.singletonMap("Authorization", "Bearer token");

        Policy policy = Policy.Builder.newInstance().target("policyTarget").build();
        CatalogItem catalogItem = CatalogItem.builder().offerId("offerId").policy(policy).build();

        JsonObject mockedJsonObject = Json.createObjectBuilder().add("", "").build();

        when(edcTransformer.transformNegotiationRequestToJson(any())).thenReturn(mockedJsonObject);

        Response response = Response.builder().responseId("negotiationId").build();
        when(httpCallService.sendRequest(any(), eq(Response.class))).thenReturn(response);

        NegotiationResponse inProgressNegotiationResponse = NegotiationResponse.builder().contractAgreementId("ContractAgreementID").state("IN_PROGRESS").build();
        NegotiationResponse finalizedNegotiationResponse = NegotiationResponse.builder().contractAgreementId("ContractAgreementID").state("FINALIZED").build();
        when(httpCallService.sendNegotiationRequest(any()))
                .thenReturn(inProgressNegotiationResponse)
                .thenReturn(finalizedNegotiationResponse);

        //WHEN
        String initializeContractNegotiation = edcService.initializeContractNegotiation(providerConnectorUrl, catalogItem, consumerEdcUrl, header);

        //THEN
        assertEquals("ContractAgreementID", initializeContractNegotiation);
        verify(edcTransformer).transformNegotiationRequestToJson(any());
        verify(httpCallService).sendRequest(any(), any());
        verify(httpCallService, times(2)).sendNegotiationRequest(any());
    }

    @Test
    void testInitializeContractNegotiationThrowExecutionException() throws IOException, InterruptedException {
        //GIVEN
        String providerConnectorUrl = "https://example.com/provider-connector";
        String consumerEdcUrl = "https://example.com/consumer-edc";
        Map<String, String> header = Collections.singletonMap("Authorization", "Bearer token");

        Policy policy = Policy.Builder.newInstance().target("policyTarget").build();
        CatalogItem catalogItem = CatalogItem.builder().offerId("offerId").policy(policy).build();

        JsonObject mockedJsonObject = Json.createObjectBuilder().add("", "").build();

        when(edcTransformer.transformNegotiationRequestToJson(any())).thenReturn(mockedJsonObject);

        Response response = Response.builder().responseId("negotiationId").build();
        when(httpCallService.sendRequest(any(), eq(Response.class))).thenReturn(response);

        when(httpCallService.sendNegotiationRequest(any())).thenThrow(new IOException());

        //WHEN
        RuntimeException runtimeException = assertThrows(
                RuntimeException.class,
                () -> edcService.initializeContractNegotiation(providerConnectorUrl, catalogItem, consumerEdcUrl, header));

        //THEN
        assertNotNull(runtimeException);
    }

    @Test
    void testInitiateHttpProxyTransferProcess() throws IOException {
        //GIVEN
        String providerConnectorControlPlaneIDSUrl = "https://example.com/provider-connector";
        String consumerEdcDataManagementUrl = "https://example.com/consumer-edc";
        Map<String, String> header = Collections.singletonMap("Authorization", "Bearer token");
        TransferProcessRequest transferProcessRequest = TransferProcessRequest.builder().build();

        JsonObject mockedJsonObject = Json.createObjectBuilder().add("", "").build();
        when(edcTransformer.transformTransferProcessRequestToJson(any())).thenReturn(mockedJsonObject);

        //WHEN
        edcService.initiateHttpProxyTransferProcess(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl
                , transferProcessRequest, header);
        //THEN
        verify(edcTransformer).transformTransferProcessRequestToJson(any());
        verify(httpCallService).sendRequest(any(), eq(Response.class));
    }
}
