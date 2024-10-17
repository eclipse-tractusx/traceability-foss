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

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Durations;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.SUBMODEL_REST_TEMPLATE;

@Slf4j
@Component
public class SubmodelClient {

    private final RestTemplate submodelRestTemplate;


    public SubmodelClient(@Qualifier(SUBMODEL_REST_TEMPLATE) RestTemplate submodelRestTemplate) {
        this.submodelRestTemplate = submodelRestTemplate;
    }

    public void createSubmodel(SubmodelRequest submodelRequest) {
        await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                            try {
                                submodelRestTemplate.exchange("/", HttpMethod.POST, new HttpEntity<>(submodelRequest), Void.class);
                                log.info("Submodel created successfully.");
                                return true;
                            } catch (Exception e) {
                                log.warn("Retrying to create submodel. Exception: {}", e.getMessage());
                                log.debug("Exception details:", e);
                                return false;
                            }
                        }
                );
    }


    public String getSubmodel(String submodelId) {
        return submodelRestTemplate.exchange("/" + submodelId, HttpMethod.GET, null, String.class).getBody();
    }
}
