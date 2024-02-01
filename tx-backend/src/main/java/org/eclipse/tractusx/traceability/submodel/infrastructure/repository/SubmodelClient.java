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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Component
public class SubmodelClient {

    private final RestTemplate submodelRestTemplate;


    public SubmodelClient(RestTemplate submodelRestTemplate) {
        this.submodelRestTemplate = submodelRestTemplate;
    }

    public void createSubmodel(String submodelId, @RequestBody String payload) {
        submodelRestTemplate.exchange("/api/submodel/data/" + submodelId, HttpMethod.POST, new HttpEntity<>(payload), Void.class);
    }


    public String getSubmodel(String submodelId) {
        return submodelRestTemplate.exchange("/api/submodel/data/" + submodelId, HttpMethod.GET, null, String.class).getBody();
    }
}
