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
package org.eclipse.tractusx.traceability.bpn.infrastructure.client;

import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BusinessPartnerResponse;
import org.eclipse.tractusx.traceability.common.properties.BpdmProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.BPDM_CLIENT_REST_TEMPLATE;

@Service
public class BpdmClient {

    private static final String PLACEHOLDER_BPID = "partnerId";
    private static final String PLACEHOLDER_ID_TYPE = "idType";
    private static final String ID_TYPE = "BPN";
    private final @Qualifier(BPDM_CLIENT_REST_TEMPLATE) RestTemplate bpdmRestTemplate;
    private final BpdmProperties bpdmProperties;

    public BpdmClient(@Qualifier(BPDM_CLIENT_REST_TEMPLATE) RestTemplate bpdmRestTemplate, BpdmProperties bpdmProperties) {
        this.bpdmRestTemplate = bpdmRestTemplate;
        this.bpdmProperties = bpdmProperties;
    }

    public BusinessPartnerResponse getBusinessPartner(final String idValue) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(bpdmProperties.getBpnEndpoint());
        final Map<String, String> values = Map.of(PLACEHOLDER_BPID, idValue, PLACEHOLDER_ID_TYPE, ID_TYPE);

        return bpdmRestTemplate.getForObject(uriBuilder.build(values), BusinessPartnerResponse.class);
    }


}
