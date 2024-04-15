/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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
package org.eclipse.tractusx.traceability.bpdm.service;

import org.eclipse.tractusx.traceability.bpdm.model.request.SiteSearchRequest;
import org.eclipse.tractusx.traceability.bpdm.model.response.SiteSearchResponse;
import org.eclipse.tractusx.traceability.bpdm.model.response.legal.LegalEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BpdmClient {

    private final RestTemplate bpdmTemplate;

    public BpdmClient(RestTemplate bpdmTemplate) {
        this.bpdmTemplate = bpdmTemplate;
    }

    public SiteSearchResponse searchForBpnlByBpns(SiteSearchRequest request) {
      return bpdmTemplate.exchange("/api/catena/sites/search", HttpMethod.POST, new HttpEntity<>(request),  new ParameterizedTypeReference<SiteSearchResponse>() {
    }).getBody();
    }

    public List<LegalEntity> searchForLegalNameByBpnl(List<String> bpnls) {
        return bpdmTemplate.exchange("/api/catena/legal-entities/search", HttpMethod.POST, new HttpEntity<>(bpnls),  new ParameterizedTypeReference<List<LegalEntity>>() {
        }).getBody();
    }
}
