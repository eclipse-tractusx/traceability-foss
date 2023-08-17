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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.CatalogItem;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.jsontransformer.EdcTransformerTraceX;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractOfferDescription;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.NegotiationRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.NegotiationResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.Response;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessRequest.DEFAULT_PROTOCOL;

@Slf4j
@Component
@RequiredArgsConstructor
public class EdcService {

    public static final MediaType JSON = MediaType.get("application/json");
    private static final String FINALIZED_STATUS = "FINALIZED";

    private final HttpCallService httpCallService;
    private final EdcProperties edcProperties;
    private final EdcTransformerTraceX edcTransformer;

    /**
     * Rest call to get all contract offer and filter notification type contract
     */
    public Catalog getCatalog(
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> header
    ) throws IOException {

        Catalog catalog = httpCallService.getCatalogForNotification(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header);

        if (catalog.getDatasets() == null || catalog.getDatasets().isEmpty()) {
            log.error("No contract found");
            throw new BadRequestException("Provider has no contract offers for us. Catalog is empty.");
        }
        return catalog;
    }


    /**
     * Prepare for contract negotiation. it will wait for while till API return agreementId
     */
    public String initializeContractNegotiation(String providerConnectorUrl, CatalogItem catalogItem, String consumerEdcUrl,
                                                Map<String, String> header) throws InterruptedException, IOException {

        final NegotiationRequest negotiationRequest = createNegotiationRequestFromCatalogItem(providerConnectorUrl + edcProperties.getIdsPath(),
                catalogItem);

        log.info(":::: Start Contract Negotiation method[initializeContractNegotiation] offerId :{}, assetId:{}", negotiationRequest.getOffer().getOfferId(), negotiationRequest.getOffer().getAssetId());

        String negotiationId = initiateNegotiation(negotiationRequest, consumerEdcUrl, header);
        NegotiationResponse negotiation = null;

        // Check negotiation state
        while (negotiation == null || negotiation.getState() == null || !negotiation.getState().equals(FINALIZED_STATUS)) {

            log.info(":::: waiting for contract to get confirmed");
            if (negotiation != null) {
                log.info("Negotation state {}", negotiation.getState());
            }

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<NegotiationResponse> scheduledFuture =
                    scheduler.schedule(() -> {
                        var url = consumerEdcUrl + edcProperties.getNegotiationPath() + "/" + negotiationId;
                        var request = new Request.Builder().url(url);
                        header.forEach(request::addHeader);

                        log.info(":::: Start call for contract agreement method [initializeContractNegotiation] URL :{}", url);

                        return httpCallService.sendNegotiationRequest(request.build());
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


    private NegotiationRequest createNegotiationRequestFromCatalogItem(final String providerConnectorUrl,
                                                                       final CatalogItem catalogItem) {
        final var contractOfferDescription = ContractOfferDescription.builder()
                .offerId(catalogItem.getOfferId())
                .assetId(catalogItem.getPolicy().getTarget())
                .policy(catalogItem.getPolicy())
                .build();
        return NegotiationRequest.builder()
                .connectorId(catalogItem.getConnectorId())
                .connectorAddress(providerConnectorUrl)
                .protocol(DEFAULT_PROTOCOL)
                .offer(contractOfferDescription)
                .build();
    }

    /**
     * Rest call for Contract negotiation and return agreementId.
     */
    private String initiateNegotiation(NegotiationRequest negotiationRequest, String consumerEdcDataManagementUrl,
                                       Map<String, String> headers) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getNegotiationPath();

        final String jsonObject = edcTransformer.transformNegotiationRequestToJson(negotiationRequest).toString();
        var requestBody = RequestBody.create(jsonObject, JSON);
        var request = new Request.Builder().url(url).post(requestBody);

        headers.forEach(request::addHeader);
        Response negotiationIdResponse = (Response) httpCallService.sendRequest(request.build(), Response.class);
        log.info(":::: Method [initiateNegotiation] Negotiation Id :{}", negotiationIdResponse.getResponseId());
        return negotiationIdResponse.getResponseId();
    }


    /**
     * Rest call for Transfer Data with HttpProxy
     */
    public void initiateHttpProxyTransferProcess(String consumerEdcDataManagementUrl,
                                                 String providerConnectorControlPlaneIDSUrl,
                                                 TransferProcessRequest transferProcessRequest,
                                                 Map<String, String> headers) throws IOException {
        var url = consumerEdcDataManagementUrl + edcProperties.getTransferPath();


        final String jsonObject = edcTransformer.transformTransferProcessRequestToJson(transferProcessRequest).toString();

        var requestBody = RequestBody.create(jsonObject, JSON);
        var request = new Request.Builder().url(url).post(requestBody);

        headers.forEach(request::addHeader);
        log.info(":::: call Transfer process with http Proxy method[initiateHttpProxyTransferProcess] agreementId:{} ,assetId :{},consumerEdcDataManagementUrl :{}, providerConnectorControlPlaneIDSUrl:{}", transferProcessRequest.getContractId(), transferProcessRequest.getAssetId(), consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl);
        httpCallService.sendRequest(request.build(), Response.class);
    }

}
