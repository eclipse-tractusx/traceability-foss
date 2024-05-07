/********************************************************************************
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
public class IrsClientRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    static Logger LOGGER = LoggerFactory.getLogger(IrsClientRestTemplateInterceptor.class);

    @Override
    @NotNull
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        String requestBody = new String(body, StandardCharsets.UTF_8);
        LOGGER.info("Request body: {}", requestBody.replaceAll("[\r\n]+", " "));
        ClientHttpResponse response = execution.execute(request, body);
        InputStreamReader isr = new InputStreamReader(
                response.getBody(), StandardCharsets.UTF_8);
        String bodys = new BufferedReader(isr).lines()
                .collect(Collectors.joining("\n"));
        LOGGER.info("Response body: {}", bodys.replaceAll("[\r\n]+", " "));
        return response;
    }
}
