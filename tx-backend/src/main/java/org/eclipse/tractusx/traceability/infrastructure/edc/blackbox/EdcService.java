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
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification.ContractNegotiationDto;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification.ContractOfferDescription;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification.NegotiationInitiateRequestDto;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification.TransferId;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer.DataAddress;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer.TransferRequestDto;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer.TransferType;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EdcService {

    private final HttpCallService httpCallService;
    private final ObjectMapper objectMapper;
    private final EdcProperties edcProperties;

    public EdcService(HttpCallService httpCallService,
                      ObjectMapper objectMapper,
                      EdcProperties edcProperties) {
        this.httpCallService = httpCallService;
        this.objectMapper = objectMapper;
        this.edcProperties = edcProperties;
    }


    /**
     * Rest call to get all contract offer and filter notification type contract
     */
    public Optional<ContractOffer> findNotificationContractOffer(
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> header,
            boolean isInitialNotification
    ) throws IOException {

        Catalog catalog = httpCallService.getCatalogFromProvider(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header);
        if (catalog.getContractOffers().isEmpty()) {
            log.error("No contract found");
            throw new BadRequestException("Provider has no contract offers for us. Catalog is empty.");
        }
        log.info(":::: Find Notification contract method[findNotificationContractOffer] total catalog ::{}", catalog.getContractOffers().size());

        if (isInitialNotification) {
            return catalog.getContractOffers().stream()
                    .filter(it -> it.getAsset().isQualityInvestigationReceive())
                    .filter(this::hasTracePolicy)
                    .findAny();
        } else {
            return catalog.getContractOffers().stream()
                    .filter(it -> it.getAsset().isQualityInvestigationUpdate())
                    .filter(this::hasTracePolicy)
                    .findAny();
        }
    }

    private boolean hasTracePolicy(ContractOffer contractOffer) {
        return contractOffer.getPolicy() != null && contractOffer.getPolicy().hasTracePolicy();
    }

    /**
     * Prepare for contract negotiation. it will wait for while till API return agreementId
     */
    public String initializeContractNegotiation(String providerConnectorUrl, String assetId, String offerId, Policy policy, String consumerEdcUrl,
                                                Map<String, String> header) throws InterruptedException, IOException {
        // Initiate negotiation
        ContractOfferDescription contractOfferDescription = new ContractOfferDescription(offerId, assetId, null, policy);
        NegotiationInitiateRequestDto contractNegotiationRequest = NegotiationInitiateRequestDto.Builder.newInstance()
                .offerId(contractOfferDescription).connectorId("provider").connectorAddress(providerConnectorUrl + edcProperties.getIdsPath())
                .protocol("ids-multipart").build();

        log.info(":::: Start Contract Negotiation method[initializeContractNegotiation] offerId :{}, assetId:{}", offerId, assetId);

        String negotiationId = initiateNegotiation(contractNegotiationRequest, consumerEdcUrl, header);
        ContractNegotiationDto negotiation = null;

        // Check negotiation state
        while (negotiation == null || !negotiation.getState().equals("CONFIRMED")) {

            log.info(":::: waiting for contract to get confirmed");
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<ContractNegotiationDto> scheduledFuture =
                    scheduler.schedule(() -> {
                        var url = consumerEdcUrl + edcProperties.getNegotiationPath() + "/" + negotiationId;
                        var request = new Request.Builder().url(url);
                        header.forEach(request::addHeader);

                        log.info(":::: Start call for contract agreement method [initializeContractNegotiation] URL :{}", url);

                        return (ContractNegotiationDto) httpCallService.sendRequest(request.build(), ContractNegotiationDto.class);
                    }, 1000, TimeUnit.MILLISECONDS);
            try {
                negotiation = scheduledFuture.get();
                scheduler.shutdown();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                if (!scheduler.isShutdown()) {
                    scheduler.shutdown();
                }
            }
        }
        return negotiation.getContractAgreementId();
    }


    /**
     * Rest call for Contract negotiation and return agreementId.
     */
    private String initiateNegotiation(NegotiationInitiateRequestDto contractOfferRequest, String consumerEdcDataManagementUrl,
                                       Map<String, String> headers) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getNegotiationPath();
        var requestBody = RequestBody.create(objectMapper.writeValueAsString(contractOfferRequest), Constants.JSON);
        var request = new Request.Builder().url(url).post(requestBody);

        headers.forEach(request::addHeader);
        request.build();
        TransferId negotiationId = (TransferId) httpCallService.sendRequest(request.build(), TransferId.class);
        log.info(":::: Method [initiateNegotiation] Negotiation Id :{}", negotiationId.getId());
        return negotiationId.getId();
    }


    /**
     * Rest call for Transfer Data with HttpProxy
     */
    public TransferId initiateHttpProxyTransferProcess(String agreementId, String assetId, String consumerEdcDataManagementUrl, String providerConnectorControlPlaneIDSUrl, Map<String, String> headers) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getTransferPath();

        DataAddress dataDestination = DataAddress.Builder.newInstance().type("HttpProxy").build();
        TransferType transferType = TransferType.Builder.transferType().contentType("application/octet-stream").isFinite(true).build();

        TransferRequestDto transferRequest = TransferRequestDto.Builder.newInstance()
                .assetId(assetId).contractId(agreementId).connectorId("provider").connectorAddress(providerConnectorControlPlaneIDSUrl)
                .protocol("ids-multipart").dataDestination(dataDestination).managedResources(false).transferType(transferType)
                .build();

        var requestBody = RequestBody.create(objectMapper.writeValueAsString(transferRequest), Constants.JSON);
        var request = new Request.Builder().url(url).post(requestBody);

        headers.forEach(request::addHeader);
        log.info(":::: call Transfer process with http Proxy method[initiateHttpProxyTransferProcess] agreementId:{} ,assetId :{},consumerEdcDataManagementUrl :{}, providerConnectorControlPlaneIDSUrl:{}", agreementId, assetId, consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl);
        return (TransferId) httpCallService.sendRequest(request.build(), TransferId.class);
    }
}
