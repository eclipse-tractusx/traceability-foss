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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.AtomicConstraint;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.LiteralExpression;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcContext;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model.CatalogRequestDTO;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Slf4j
@Component
public class HttpCallService {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final EdcProperties edcProperties;

    public HttpCallService(OkHttpClient httpClient, ObjectMapper objectMapper, EdcProperties edcProperties) {
        this.httpClient = withIncreasedTimeout(httpClient);
        this.objectMapper = objectMapper;
        this.edcProperties = edcProperties;
        objectMapper.registerSubtypes(AtomicConstraint.class, LiteralExpression.class);
    }

    private static OkHttpClient withIncreasedTimeout(OkHttpClient httpClient) {
        return httpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build();
    }


    public Catalog getCatalogFromProvider(
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> headers
    ) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getCatalogPath();
        MediaType mediaType = MediaType.parse("application/json");
        CatalogRequestDTO catalogRequestDTO = new CatalogRequestDTO(providerConnectorControlPlaneIDSUrl, "dataspace-protocol-http", new EdcContext("https://w3id.org/edc/v0.0.1/ns/"));
        var request = new Request.Builder().url(url).post(RequestBody.create(mediaType, objectMapper.writeValueAsString(catalogRequestDTO)));
        headers.forEach(request::addHeader);

        return (Catalog) sendRequest(request.build(), Catalog.class);
    }


    public Object sendRequest(Request request, Class<?> responseObject) throws IOException {
        log.info("Requesting {} {}...", request.method(), request.url());
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            String res = body.string();
            return objectMapper.readValue(res, responseObject);
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendRequest(Request request) throws IOException {
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }
        } catch (Exception e) {
            throw e;
        }
    }


    public HttpUrl getUrl(String connectorUrl, String subUrl, MultivaluedMap<String, String> parameters) {
        var url = connectorUrl;

        if (subUrl != null && !subUrl.isEmpty()) {
            url = url + "/" + subUrl;
        }

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        if (parameters == null) {
            return httpBuilder.build();
        }

        for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
            for (String value : param.getValue()) {
                httpBuilder = httpBuilder.addQueryParameter(param.getKey(), value);
            }
        }

        return httpBuilder.build();
    }
}
