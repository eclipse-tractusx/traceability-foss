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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.Dataset;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.configuration.JsonLdConfigurationTraceX;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.testdata.CatalogTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestigationsEDCFacadeTest {

    @Mock
    EdcService edcService;

    @Mock
    HttpCallService httpCallService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

    @Mock
    EDCCatalogFacade edcCatalogFacade;

    @Mock
    EdcProperties edcProperties;

    @InjectMocks
    InvestigationsEDCFacade investigationsEDCFacade;

    @Test
    void test_startEDCTransfer() throws IOException, InterruptedException {
        //GIVEN
        String receiverEdcUrl = "https://example.com/receiver-edcUrl";
        String senderEdcUrl = "https://example.com/sender-edcUrl";

        QualityNotificationMessage qualityNotificationMessage = NotificationTestDataFactory
                .createNotificationTestData(QualityNotificationType.INVESTIGATION);

        when(edcProperties.getApiAuthKey()).thenReturn("x-api-key");
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        Catalog catalog = CatalogTestDataFactory.createCatalogTestData();
        when(edcService.getCatalog(anyString(), anyString(), any())).thenReturn(catalog);

        when(edcService.initializeContractNegotiation(anyString(), any(), anyString(), any()))
                .thenReturn("negotiationId");

        Algorithm algorithm = Algorithm.HMAC256("Catena-X");
        String jwtToken = JWT.create().withExpiresAt(Instant.now()).sign(algorithm);
        EndpointDataReference endpointDataReference = EndpointDataReference.Builder.newInstance()
                .endpoint("")
                .authCode(jwtToken)
                .authKey("authKey")
                .build();
        when(endpointDataReferenceCache.get(anyString())).thenReturn(endpointDataReference);

        when(objectMapper.writeValueAsString(any())).thenReturn("someValidJson");

        when(httpCallService.getUrl(any(), any(), any())).thenReturn(HttpUrl.get("https://w3id.org"));

        //WHEN
        investigationsEDCFacade.startEDCTransfer(qualityNotificationMessage, receiverEdcUrl, senderEdcUrl);

        //THEN
        verify(edcProperties).getApiAuthKey();
        verify(edcService).getCatalog(anyString(), anyString(), any());
        verify(edcService).initializeContractNegotiation(anyString(), any(), anyString(), any());
        verify(endpointDataReferenceCache, times(2)).get(anyString());
        verify(objectMapper).writeValueAsString(any());
        verify(httpCallService).getUrl(any(), any(), any());
    }

    @Test
    void test_startEDCTransfer_with_catalog_properties() throws IOException, InterruptedException {
        //GIVEN
        String receiverEdcUrl = "https://example.com/receiver-edcUrl";
        String senderEdcUrl = "https://example.com/sender-edcUrl";

        QualityNotificationMessage qualityNotificationMessage = NotificationTestDataFactory
                .createNotificationTestData(QualityNotificationType.INVESTIGATION);

        when(edcProperties.getApiAuthKey()).thenReturn("x-api-key");
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        Map<String, Object> properties = Map.of(JsonLdConfigurationTraceX.NAMESPACE_EDC_PARTICIPANT_ID, "participantId");
        Catalog catalog = CatalogTestDataFactory.createCatalogTestData(properties);
        when(edcService.getCatalog(anyString(), anyString(), any())).thenReturn(catalog);

        when(edcService.initializeContractNegotiation(anyString(), any(), anyString(), any()))
                .thenReturn("negotiationId");

        Algorithm algorithm = Algorithm.HMAC256("Catena-X");
        String jwtToken = JWT.create().withExpiresAt(Instant.now()).sign(algorithm);
        EndpointDataReference endpointDataReference = EndpointDataReference.Builder.newInstance()
                .endpoint("")
                .authCode(jwtToken)
                .authKey("authKey")
                .build();
        when(endpointDataReferenceCache.get(anyString())).thenReturn(endpointDataReference);

        when(objectMapper.writeValueAsString(any())).thenReturn("someValidJson");

        when(httpCallService.getUrl(any(), any(), any())).thenReturn(HttpUrl.get("https://w3id.org"));

        //WHEN
        investigationsEDCFacade.startEDCTransfer(qualityNotificationMessage, receiverEdcUrl, senderEdcUrl);

        //THEN
        verify(edcProperties).getApiAuthKey();
        verify(edcService).getCatalog(anyString(), anyString(), any());
        verify(edcService).initializeContractNegotiation(anyString(), any(), anyString(), any());
        verify(endpointDataReferenceCache, times(2)).get(anyString());
        verify(objectMapper).writeValueAsString(any());
        verify(httpCallService).getUrl(any(), any(), any());
    }

    @Test
    void test_startEDCTransfer_throws_BadRequestException() throws IOException {
        //GIVEN
        String receiverEdcUrl = "https://example.com/receiver-edcUrl";
        String senderEdcUrl = "https://example.com/sender-edcUrl";

        QualityNotificationMessage qualityNotificationMessage = NotificationTestDataFactory
                .createNotificationTestData(QualityNotificationType.INVESTIGATION);

        when(edcProperties.getApiAuthKey()).thenReturn("x-api-key");
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        Catalog catalog = Catalog.Builder.newInstance().datasets(Collections.emptyList()).build();
        when(edcService.getCatalog(anyString(), anyString(), any())).thenReturn(catalog);

        //WHEN
        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> investigationsEDCFacade.startEDCTransfer(qualityNotificationMessage, receiverEdcUrl, senderEdcUrl));

        //THEN
        verify(edcProperties).getApiAuthKey();
        verify(edcService).getCatalog(anyString(), anyString(), any());
        assertEquals("The dataset from the catalog is empty.", badRequestException.getMessage());
    }

    @Test
    void test_startEDCTransfer_throws_BadRequestException_when_catalogItem_isEmpty() throws IOException {
        //GIVEN
        String receiverEdcUrl = "https://example.com/receiver-edcUrl";
        String senderEdcUrl = "https://example.com/sender-edcUrl";

        QualityNotificationMessage qualityNotificationMessage = NotificationTestDataFactory
                .createNotificationTestData(QualityNotificationType.INVESTIGATION);

        when(edcProperties.getApiAuthKey()).thenReturn("x-api-key");
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        Dataset.Builder datasetBuilder = Dataset.Builder.newInstance()
                .property("https://w3id.org/edc/v0.0.1/ns/notificationtype", "invalidNotificationType")
                .property("https://w3id.org/edc/v0.0.1/ns/notificationmethod", "invalidNotificationMethod")
                .property("https://w3id.org/edc/v0.0.1/ns/id", "id");

        Catalog catalog = CatalogTestDataFactory.createCatalogTestData(datasetBuilder);
        when(edcService.getCatalog(anyString(), anyString(), any())).thenReturn(catalog);

        //WHEN
        NoCatalogItemException badRequestException = assertThrows(NoCatalogItemException.class,
                () -> investigationsEDCFacade.startEDCTransfer(qualityNotificationMessage, receiverEdcUrl, senderEdcUrl));

        //THEN
        verify(edcProperties).getApiAuthKey();
        verify(edcService).getCatalog(anyString(), anyString(), any());
        assertEquals("No Catalog Item in catalog found.", badRequestException.getMessage());

    }

    @Test
    void test_startEDCTransfer_throws_BadRequestException_when_IOException_is_thrown() throws IOException {
        //GIVEN
        String receiverEdcUrl = "https://example.com/receiver-edcUrl";
        String senderEdcUrl = "https://example.com/sender-edcUrl";

        QualityNotificationMessage qualityNotificationMessage = NotificationTestDataFactory
                .createNotificationTestData(QualityNotificationType.INVESTIGATION);

        when(edcProperties.getApiAuthKey()).thenReturn("x-api-key");
        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of());

        when(edcService.getCatalog(anyString(), anyString(), any())).thenThrow(IOException.class);

        //WHEN
        BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> investigationsEDCFacade.startEDCTransfer(qualityNotificationMessage, receiverEdcUrl, senderEdcUrl));

        //THEN
        verify(edcProperties).getApiAuthKey();
        verify(edcService).getCatalog(anyString(), anyString(), any());
        assertEquals("EDC Data Transfer fail.", badRequestException.getMessage());
    }
}
