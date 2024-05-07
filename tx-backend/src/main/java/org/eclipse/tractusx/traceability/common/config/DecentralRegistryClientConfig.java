/********************************************************************************
 * Copyright (c)  2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.common.config;

import org.eclipse.tractusx.irs.edc.client.EdcSubmodelFacade;
import org.eclipse.tractusx.irs.edc.client.exceptions.EdcClientException;
import org.eclipse.tractusx.irs.registryclient.decentral.EdcEndpointReferenceRetriever;
import org.eclipse.tractusx.irs.registryclient.decentral.EdcRetrieverException;
import org.eclipse.tractusx.irs.registryclient.decentral.EndpointDataForConnectorsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.zaxxer.hikari.hibernate.HikariConfigurationUtil.CONFIG_PREFIX;
@Configuration
public class DecentralRegistryClientConfig {


    // TODO fix in irs
    @Bean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "type", havingValue = "decentral")
    public EndpointDataForConnectorsService endpointDataForConnectorsService(final EdcSubmodelFacade facade) {

        final EdcEndpointReferenceRetriever edcEndpointReferenceRetriever = (edcConnectorEndpoint, assetType, assetValue, bpn) -> {
            try {
                return facade.getEndpointReferencesForAsset(edcConnectorEndpoint, assetType, assetValue, bpn);
            } catch (EdcClientException e) {
                throw new EdcRetrieverException(e);
            }
        };

        return new EndpointDataForConnectorsService(edcEndpointReferenceRetriever);
    }
}
