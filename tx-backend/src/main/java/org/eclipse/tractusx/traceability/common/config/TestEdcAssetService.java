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

package org.eclipse.tractusx.traceability.common.config;

import jakarta.json.JsonObject;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.tractusx.irs.edc.client.EdcConfiguration;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.transformer.EdcTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
public class TestEdcAssetService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TestEdcAssetService.class);
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_POLICY_ID = "use-eu";
    private static final String DEFAULT_METHOD = "POST";
    private static final String ASSET_CREATION_DATA_ADDRESS_BASE_URL = "https://w3id.org/edc/v0.0.1/ns/baseUrl";
    private static final String ASSET_CREATION_DATA_ADDRESS_PROXY_METHOD = "https://w3id.org/edc/v0.0.1/ns/proxyMethod";
    private static final String ASSET_CREATION_DATA_ADDRESS_PROXY_BODY = "https://w3id.org/edc/v0.0.1/ns/proxyBody";
    private static final String ASSET_CREATION_DATA_ADDRESS_PROXY_PATH = "https://w3id.org/edc/v0.0.1/ns/proxyPath";
    private static final String ASSET_CREATION_DATA_ADDRESS_PROXY_QUERY_PARAMS = "https://w3id.org/edc/v0.0.1/ns/proxyQueryParams";
    private static final String ASSET_CREATION_DATA_ADDRESS_METHOD = "https://w3id.org/edc/v0.0.1/ns/method";
    private static final String ASSET_CREATION_PROPERTY_DESCRIPTION = "https://w3id.org/edc/v0.0.1/ns/description";
    private static final String ASSET_CREATION_PROPERTY_CONTENT_TYPE = "https://w3id.org/edc/v0.0.1/ns/contenttype";
    private static final String ASSET_CREATION_PROPERTY_POLICY_ID = "https://w3id.org/edc/v0.0.1/ns/policy-id";
    private static final String ASSET_CREATION_PROPERTY_TYPE = "https://w3id.org/edc/v0.0.1/ns/type";
    private static final String ASSET_CREATION_PROPERTY_NOTIFICATION_TYPE = "https://w3id.org/edc/v0.0.1/ns/notificationtype";
    private static final String ASSET_CREATION_PROPERTY_NOTIFICATION_METHOD = "https://w3id.org/edc/v0.0.1/ns/notificationmethod";
    private final EdcTransformer edcTransformer;
    private final EdcConfiguration config;
    private final RestTemplate restTemplate;

    public String createDtrAsset(String baseUrl, String assetId) throws CreateEdcAssetException {
        Asset request = this.createDtrAssetRequest(assetId, baseUrl);
        return this.sendRequest(request);
    }

    public String createSubmodelAsset(String baseUrl, String assetId) throws CreateEdcAssetException {
        Asset request = this.createSubmodelAssetRequest(assetId, baseUrl);
        return this.sendRequest(request);
    }

    private String sendRequest(Asset request) throws CreateEdcAssetException {
        JsonObject transformedPayload = this.edcTransformer.transformAssetToJson(request);

        try {
            ResponseEntity<String> createEdcDataAssetResponse = this.restTemplate.postForEntity(this.config.getControlplane().getEndpoint().getAsset(), transformedPayload.toString(), String.class, new Object[0]);
            HttpStatusCode responseCode = createEdcDataAssetResponse.getStatusCode();

            if (responseCode.value() == HttpStatus.OK.value()) {
                return request.getId();
            }
        } catch (HttpClientErrorException var5) {
            if (var5.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                return request.getId();
            }
            throw new CreateEdcAssetException(var5);
        }

        throw new CreateEdcAssetException("Failed to create asset %s".formatted(request.getId()));
    }

    private Asset createDtrAssetRequest(String assetId, String baseUrl) {
        Map<String, Object> properties = Map.of("https://w3id.org/edc/v0.0.1/ns/description", "Digital Twin Registry Asset", "https://w3id.org/edc/v0.0.1/ns/type", "data.core.digitalTwinRegistry");
        DataAddress dataAddress = DataAddress.Builder.newInstance().type("DataAddress").property("https://w3id.org/edc/v0.0.1/ns/type", "HttpData").property("https://w3id.org/edc/v0.0.1/ns/baseUrl", baseUrl).property("https://w3id.org/edc/v0.0.1/ns/proxyMethod", Boolean.TRUE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyBody", Boolean.TRUE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyPath", Boolean.TRUE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyQueryParams", Boolean.TRUE.toString()).build();
        return org.eclipse.edc.spi.types.domain.asset.Asset.Builder.newInstance().id(assetId).contentType("Asset").properties(properties).dataAddress(dataAddress).build();
    }

    private Asset createSubmodelAssetRequest(String assetId, String baseUrl) {
        Map<String, Object> properties = Map.of("https://w3id.org/edc/v0.0.1/ns/description", "Submodel Server Asset");
        DataAddress dataAddress = DataAddress.Builder.newInstance().type("DataAddress").property("https://w3id.org/edc/v0.0.1/ns/type", "HttpData").property("https://w3id.org/edc/v0.0.1/ns/baseUrl", baseUrl).property("https://w3id.org/edc/v0.0.1/ns/proxyMethod", Boolean.FALSE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyBody", Boolean.FALSE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyPath", Boolean.TRUE.toString()).property("https://w3id.org/edc/v0.0.1/ns/proxyQueryParams", Boolean.FALSE.toString()).build();
        return org.eclipse.edc.spi.types.domain.asset.Asset.Builder.newInstance().id(assetId).contentType("Asset").properties(properties).dataAddress(dataAddress).build();
    }
}
