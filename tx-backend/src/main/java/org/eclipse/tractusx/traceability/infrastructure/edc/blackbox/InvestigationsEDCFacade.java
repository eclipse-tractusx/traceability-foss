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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationFactory;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestigationsEDCFacade {

    private static final MediaType JSON = MediaType.get("application/json");

    private final EdcService edcService;

    private final HttpCallService httpCallService;

    private final ObjectMapper objectMapper;

    private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

    private final EdcProperties edcProperties;

    public void startEDCTransfer(QualityNotificationMessage notification, String receiverEdcUrl, String senderEdcUrl) {
        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", edcProperties.getApiAuthKey());
        try {
            notification.setEdcUrl(receiverEdcUrl);

            log.info(":::: Find Notification contract method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}", senderEdcUrl, receiverEdcUrl);
            Optional<ContractOffer> contractOffer = edcService.findNotificationContractOffer(
                    senderEdcUrl,
                    receiverEdcUrl + edcProperties.getIdsPath(),
                    header,
                    notification.getIsInitial()
            );

            if (contractOffer.isEmpty()) {
                log.info("No Notification contractOffer found");
                throw new BadRequestException("No notification contract offer found.");
            }

            log.info(":::: Initialize Contract Negotiation method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}", senderEdcUrl, receiverEdcUrl);
            String agreementId = edcService.initializeContractNegotiation(
                    receiverEdcUrl,
                    contractOffer.get().getAsset().getId(),
                    contractOffer.get().getId(),
                    contractOffer.get().getPolicy(),
                    senderEdcUrl,
                    header
            );

            endpointDataReferenceCache.storeAgreementId(agreementId);
            log.info(":::: Contract Agreed method[startEDCTransfer] agreementId :{}", agreementId);

            if (StringUtils.hasLength(agreementId)) {
                notification.setContractAgreementId(agreementId);
            }

            EndpointDataReference dataReference = endpointDataReferenceCache.get(agreementId);
            boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
            if (!validDataReference) {
                log.info(":::: Invalid Data Reference :::::");
                if (dataReference != null) {
                    endpointDataReferenceCache.remove(agreementId);
                }

                log.info(":::: initialize Transfer process with http Proxy :::::");
                // Initiate transfer process
                edcService.initiateHttpProxyTransferProcess(agreementId, contractOffer.get().getAsset().getId(),
                        senderEdcUrl,
                        receiverEdcUrl + edcProperties.getIdsPath(),
                        header
                );
                dataReference = getDataReference(agreementId);
            }

            Request notificationRequest = buildNotificationRequest(notification, senderEdcUrl, dataReference);

            httpCallService.sendRequest(notificationRequest);

            log.info(":::: EDC Data Transfer Completed :::::");
        } catch (IOException e) {
            log.error("EDC Data Transfer fail", e);

            throw new BadRequestException("EDC Data Transfer fail");
        } catch (InterruptedException e) {
            log.error("Exception", e);
            Thread.currentThread().interrupt();
        }
    }

    private Request buildNotificationRequest(QualityNotificationMessage notification, String senderEdcUrl, EndpointDataReference dataReference) throws JsonProcessingException {
        EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(senderEdcUrl, notification);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String body = objectMapper.writeValueAsString(edcNotification);
        HttpUrl url = httpCallService.getUrl(dataReference.getEndpoint(), null, null);
        log.info(":::: Send notification Data  body :{}, dataReferenceEndpoint :{}", body, dataReference.getEndpoint());
        return new Request.Builder()
                .url(url)
                .addHeader(dataReference.getAuthKey(), dataReference.getAuthCode())
                .addHeader("Content-Type", JSON.type())
                .post(RequestBody.create(body, JSON))
                .build();
    }

    private EndpointDataReference getDataReference(String agreementId) throws InterruptedException {
        EndpointDataReference dataReference = null;
        var waitTimeout = 20;
        while (dataReference == null && waitTimeout > 0) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<EndpointDataReference> scheduledFuture =
                    scheduler.schedule(() -> endpointDataReferenceCache.get(agreementId), 30, TimeUnit.SECONDS);
            try {
                dataReference = scheduledFuture.get();
                waitTimeout--;
                scheduler.shutdown();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                if (!scheduler.isShutdown()) {
                    scheduler.shutdown();
                }
            }
        }
        if (dataReference == null) {
            throw new BadRequestException("Did not receive callback within 30 seconds from consumer edc.");
        }
        return dataReference;
    }


}
