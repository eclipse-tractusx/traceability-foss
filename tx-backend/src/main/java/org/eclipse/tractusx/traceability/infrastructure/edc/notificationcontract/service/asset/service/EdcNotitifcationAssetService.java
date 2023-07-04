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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.NotificationMethod;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.NotificationType;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.CreateEdcAssetException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcAsset;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcAssetProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcContext;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcCreateDataAssetRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcDataAddress;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.EdcDataAddressProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration.EdcConstants.EDC_CONTEXT;
import static org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration.EdcRestTemplateConfiguration.EDC_REST_TEMPLATE;

@Slf4j
@Component
public class EdcNotitifcationAssetService {

    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_POLICY_ID = "use-eu";
    private static final String DEFAULT_METHOD = "POST";
    private static final String DEFAULT_DATA_ADDRESS_PROPERTY_TYPE = "HttpData";
    private static final String TRACE_FOSS_QUALITY_NOTIFICATION_INVESTIGATION_URL_TEMPLATE = "/api/qualitynotifications/%s";
    private static final String TRACE_FOSS_QUALITY_NOTIFICATION_ALERT_URL_TEMPLATE = "/api/qualityalerts/%s";

    private final TraceabilityProperties traceabilityProperties;
    private final RestTemplate restTemplate;
    private final EdcProperties edcProperties;

    @Autowired
    public EdcNotitifcationAssetService(TraceabilityProperties traceabilityProperties, @Qualifier(EDC_REST_TEMPLATE) RestTemplate restTemplate, EdcProperties edcProperties) {
        this.traceabilityProperties = traceabilityProperties;
        this.restTemplate = restTemplate;
        this.edcProperties = edcProperties;
    }

    public String createNotificationAsset(NotificationMethod notificationMethod, NotificationType notificationType) {
        String notificationMethodValue = notificationMethod.getValue();

        final String template = notificationType.equals(NotificationType.QUALITY_ALERT) ? TRACE_FOSS_QUALITY_NOTIFICATION_ALERT_URL_TEMPLATE : TRACE_FOSS_QUALITY_NOTIFICATION_INVESTIGATION_URL_TEMPLATE;

        String notificationAssetId = UUID.randomUUID().toString();

        EdcDataAddressProperties edcDataAddressProperties = new EdcDataAddressProperties(
                traceabilityProperties.getUrl() + template.formatted(notificationMethodValue),
                "true",
                DEFAULT_METHOD,
                "true",
                DEFAULT_DATA_ADDRESS_PROPERTY_TYPE
        );

        EdcDataAddress edcDataAddress = new EdcDataAddress(edcDataAddressProperties);

        String description = "endpoint to %s %s".formatted(notificationMethodValue, notificationType.getValue());

        EdcAssetProperties edcAssetProperties = new EdcAssetProperties(
                description,
                DEFAULT_CONTENT_TYPE,
                DEFAULT_POLICY_ID,
                notificationType.getValue(),
                notificationType.getValue(),
                notificationMethodValue
        );

        EdcAsset edcAsset = EdcAsset.builder().assetId(notificationAssetId).edcAssetProperties(edcAssetProperties).build();
        EdcContext edcContext = new EdcContext(EDC_CONTEXT);
        EdcCreateDataAssetRequest createDataAssetRequest = EdcCreateDataAssetRequest.builder().edcAsset(edcAsset).edcDataAddress(edcDataAddress).edcContext(edcContext).build();

        final ResponseEntity<String> createEdcDataAssetResponse;

        try {
            createEdcDataAssetResponse = restTemplate.postForEntity(
                    edcProperties.getAssetsPath(),
                    createDataAssetRequest,
                    String.class
            );
        } catch (RestClientException e) {
            log.error("Failed to create EDC notification asset for {} method. Reason: ", notificationMethod, e);
            throw new CreateEdcAssetException(e);
        }

        HttpStatusCode responseCode = createEdcDataAssetResponse.getStatusCode();

        if (responseCode.value() == 409) {
            log.info("{} notification asset already exists in the EDC", notificationAssetId);

            return notificationAssetId;
        }

        if (responseCode.value() == 200) {
            return notificationAssetId;
        }

        log.error("Failed to create EDC notification asset for {} method. Body: {}, status: {}", notificationMethodValue, createEdcDataAssetResponse.getBody(), createEdcDataAssetResponse.getStatusCode());

        throw new CreateEdcAssetException("Failed to create EEC notification asset for %s method".formatted(notificationMethodValue));
    }

    public void deleteNotificationAsset(String notificationAssetId) {
        String deleteUri = UriComponentsBuilder.fromPath(edcProperties.getAssetsPath())
                .pathSegment("{notificationAssetId}")
                .buildAndExpand(notificationAssetId)
                .toUriString();

        try {
            restTemplate.delete(deleteUri);
        } catch (RestClientException e) {
            log.error("Failed to delete EDC notification asset {}. Reason: ", notificationAssetId, e);
        }
    }
}
