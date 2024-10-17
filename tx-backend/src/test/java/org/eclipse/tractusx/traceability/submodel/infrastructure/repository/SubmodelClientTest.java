/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmodelClientTest {

    @Mock
    private RestTemplate submodelRestTemplate;

    private SubmodelClient submodelClient;
    private ObjectMapper objectMapper;
    @Captor
    private ArgumentCaptor<HttpEntity<SubmodelRequest>> httpEntityArgumentCaptor;

    @BeforeEach
    public void setUp() {
        submodelClient = new SubmodelClient(submodelRestTemplate);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldCreateSubmodelWithExpectedRequestObject() {
        // given
        String submodelData = "{\n" +
                "\t\"localIdentifiers\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"value\": \"BPNL00000003CML1\",\n" +
                "\t\t\t\"key\": \"manufacturerId\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"value\": \"OMAOYGBDTSRCMYSCX\",\n" +
                "\t\t\t\"key\": \"partInstanceId\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"value\": \"OMAOYGBDTSRCMYSCX\",\n" +
                "\t\t\t\"key\": \"van\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"manufacturingInformation\": {\n" +
                "\t\t\"date\": \"2018-09-28T04:15:57.000Z\",\n" +
                "\t\t\"country\": \"DEU\"\n" +
                "\t},\n" +
                "\t\"catenaXId\": \"urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36b99\",\n" +
                "\t\"partTypeInformation\": {\n" +
                "\t\t\"manufacturerPartId\": \"3500076-05\",\n" +
                "\t\t\"partClassification\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"classificationStandard\": \"classificationStandard\",\n" +
                "\t\t\t\t\"classificationID\": \"classificationID\",\n" +
                "\t\t\t\t\"classificationDescription\": \"classificationDescription\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"nameAtManufacturer\": \"a/dev Vehicle Hybrid\"\n" +
                "\t}\n" +
                "}";

        Map<String, Object> jsonData = null;
        try {
            jsonData = objectMapper.readValue(submodelData, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        SubmodelRequest expectedSubmodelRequest = SubmodelRequest.builder().submodelId(UUID.randomUUID().toString()).data(jsonData).build();

        // Mock the exchange method to throw an exception the first few times and then succeed
        given(submodelRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .willReturn(ResponseEntity.ok().build());  // Successful after retries

        // when
        submodelClient.createSubmodel(expectedSubmodelRequest);

        // then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    // Verify that the exchange method was called at least once (because of retries)
                    verify(submodelRestTemplate, times(1))
                            .exchange(anyString(), any(HttpMethod.class), httpEntityArgumentCaptor.capture(), any(Class.class));
                });

        // Validate the captured request body
        HttpEntity<SubmodelRequest> capturedHttpEntity = httpEntityArgumentCaptor.getValue();
        SubmodelRequest actualSubmodelRequest = capturedHttpEntity.getBody();

        assertEquals(expectedSubmodelRequest, actualSubmodelRequest);
    }
}
