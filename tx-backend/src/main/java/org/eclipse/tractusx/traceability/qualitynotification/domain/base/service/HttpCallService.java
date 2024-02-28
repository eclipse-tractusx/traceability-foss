/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.BadRequestException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Slf4j
@Component
public class HttpCallService {

    private final RestTemplate edcNotificationTemplate;

    public HttpCallService(RestTemplate edcNotificationTemplate) {
        this.edcNotificationTemplate = edcNotificationTemplate;
    }


    public void sendRequest(final EdcNotificationRequest request) {
        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), request.getHeaders());
        try {
            var response = edcNotificationTemplate.exchange(request.getUrl(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });
            log.info("Control plane responded with status: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException(format("Control plane responded with: %s", response.getStatusCode()));
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw e;
        }
    }
}
