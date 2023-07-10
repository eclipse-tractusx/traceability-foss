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
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.jsontransformer.EdcTransformerTraceX;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
