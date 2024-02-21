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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsClientTest {

    @InjectMocks
    private IrsClient irsClient;

    @Mock
    private RestTemplate irsAdminTemplate;

    @Mock
    private RestTemplate irsRegularTemplate;

    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> methodCaptor;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> entityCaptor;

    @Test
    void testRegisterPolicy() {
        // Arrange
        OffsetDateTime validUntil = OffsetDateTime.now();
        when(traceabilityProperties.getValidUntil()).thenReturn(validUntil);

        // Act
        irsClient.registerPolicy();

        // Assert
        verify(irsAdminTemplate).exchange(urlCaptor.capture(), methodCaptor.capture(), entityCaptor.capture(), eq(Void.class));

        assertEquals("/irs/policies", urlCaptor.getValue());
        assertEquals(HttpMethod.POST, methodCaptor.getValue());

        HttpEntity<?> requestEntity = entityCaptor.getValue();
        assertNotNull(requestEntity);

        RegisterPolicyRequest requestBody = (RegisterPolicyRequest) requestEntity.getBody();
        assertNotNull(requestBody);
        assertNotNull(requestBody.payload().policy());
    }
}
