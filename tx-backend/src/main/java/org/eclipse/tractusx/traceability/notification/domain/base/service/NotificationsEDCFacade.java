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
package org.eclipse.tractusx.traceability.notification.domain.base.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.edc.client.ContractNegotiationService;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.EndpointDataReferenceStorage;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractService;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.BadRequestException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoEndpointDataReferenceException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.EDC_NOTIFICATION_TEMPLATE;

@Slf4j
@Component
@Transactional(dontRollbackOn = {ContractNegotiationException.class, NoCatalogItemException.class, SendNotificationException.class, NoEndpointDataReferenceException.class})
public class NotificationsEDCFacade {

    public static final String DEFAULT_PROTOCOL = "dataspace-protocol-http";

    private final ObjectMapper objectMapper;

    private final EdcProperties edcProperties;


    private final RestTemplate edcNotificationTemplate;
    private final EDCCatalogFacade edcCatalogFacade;
    private final ContractNegotiationService contractNegotiationService;
    private final EndpointDataReferenceStorage endpointDataReferenceStorage;
    private final ContractService contractNotificationServiceImpl;

    public NotificationsEDCFacade(ObjectMapper objectMapper,
                                  EdcProperties edcProperties,
                                  @Qualifier(EDC_NOTIFICATION_TEMPLATE) RestTemplate edcNotificationTemplate,
                                  EDCCatalogFacade edcCatalogFacade,
                                  ContractNegotiationService contractNegotiationService,
                                  EndpointDataReferenceStorage endpointDataReferenceStorage,
                                  @Qualifier("contractNotificationServiceImpl") ContractService contractNotificationServiceImpl) {
        this.objectMapper = objectMapper;
        this.edcProperties = edcProperties;
        this.edcNotificationTemplate = edcNotificationTemplate;
        this.edcCatalogFacade = edcCatalogFacade;
        this.contractNegotiationService = contractNegotiationService;
        this.endpointDataReferenceStorage = endpointDataReferenceStorage;
        this.contractNotificationServiceImpl = contractNotificationServiceImpl;
    }

    public static final String CX_TAXO_QUALITY_INVESTIGATION_RECEIVE = "https://w3id.org/catenax/taxonomy#ReceiveQualityInvestigationNotification";
    public static final String CX_TAXO_QUALITY_INVESTIGATION_UPDATE = "https://w3id.org/catenax/taxonomy#UpdateQualityInvestigationNotification";
    public static final String CX_TAXO_QUALITY_ALERT_RECEIVE = "https://w3id.org/catenax/taxonomy#ReceiveQualityAlertNotification";
    public static final String CX_TAXO_QUALITY_ALERT_UPDATE = "https://w3id.org/catenax/taxonomy#UpdateQualityAlertNotification";

    public void startEdcTransfer(
            final NotificationMessage notificationMessage,
            final String receiverEdcUrl,
            final String senderEdcUrl,
            final Notification notification) {

        CatalogItem catalogItem = getCatalogItem(notificationMessage, receiverEdcUrl);

        String contractAgreementId = negotiateContractAgreement(receiverEdcUrl, catalogItem, notificationMessage.getSentTo());

        final EndpointDataReference dataReference = endpointDataReferenceStorage.get(contractAgreementId)
                .orElseThrow(() -> new NoEndpointDataReferenceException("No EndpointDataReference was found"));

        notificationMessage.setContractAgreementId(contractAgreementId);
        try {
            contractNotificationServiceImpl.saveContractAgreements(List.of(contractAgreementId), ContractType.NOTIFICATION);
        } catch (Exception e) {
            log.warn("Could not save contractAgreementId for notification {}", e.getMessage());
        }

        try {
            EdcNotificationRequest notificationRequest = toEdcNotificationRequest(notificationMessage, senderEdcUrl, dataReference, notification);
            sendRequest(notificationRequest);
        } catch (Exception e) {
            throw new SendNotificationException("Failed to send notificationMessage.", e);
        }
    }

    private String negotiateContractAgreement(final String receiverEdcUrl, final CatalogItem catalogItem, String receiverBpn) {

        try {
            log.info("Negotiation of contract agreement for receiverEdcUrl {} and catalogItem {}", receiverEdcUrl, catalogItem);
            return Optional.ofNullable(contractNegotiationService.negotiate(receiverEdcUrl + edcProperties.getIdsPath(), catalogItem, null, receiverBpn))
                    .orElseThrow()
                    .getContractAgreementId();
        } catch (Exception e) {
            throw new ContractNegotiationException("Failed to negotiate contract agreement: " + e.getMessage(), e);
        }
    }

    private CatalogItem getCatalogItem(final NotificationMessage notification, final String receiverEdcUrl) {
        try {

            String taxoValue;
            if (NotificationType.ALERT.equals(notification.getType()) && notification.getNotificationStatus().equals(NotificationStatus.SENT)) {
                taxoValue = CX_TAXO_QUALITY_ALERT_RECEIVE;
            } else if (!NotificationType.ALERT.equals(notification.getType()) && notification.getNotificationStatus().equals(NotificationStatus.SENT)) {
                taxoValue = CX_TAXO_QUALITY_INVESTIGATION_RECEIVE;
            } else if (NotificationType.ALERT.equals(notification.getType())) {
                taxoValue = CX_TAXO_QUALITY_ALERT_UPDATE;
            } else {
                taxoValue = CX_TAXO_QUALITY_INVESTIGATION_UPDATE;
            }

            return edcCatalogFacade.fetchCatalogItems(
                            CatalogRequest.Builder.newInstance()
                                    .protocol(DEFAULT_PROTOCOL)
                                    .counterPartyAddress(receiverEdcUrl + edcProperties.getIdsPath())
                                    .counterPartyId(notification.getSentTo())
                                    .querySpec(QuerySpec.Builder.newInstance()
                                            // https://github.com/eclipse-tractusx/traceability-foss/issues/978
                                            // Probably:
                                            // leftOperand = 'http://purl.org/dc/terms/type'.'@id'
                                            // rightOperand = https://w3id.org/catenax/taxonomy#ReceiveQualityAlertNotification (make sure to check the input for the correct one Receive/Update and Alert or Investigation
                                            // The types are all in the ticket documented
                                            .filter(
                                                    List.of(new Criterion("'http://purl.org/dc/terms/type'.'@id'", "=", taxoValue))
                                            )
                                            .build())
                                    .build()
                    ).stream()
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            log.error("Exception was thrown while requesting catalog items from Lib", e);
            throw new NoCatalogItemException(e);
        }
    }

    // TODO this method should be completly handled by EDCNotificationFactory.createEdcNotification which is part of this method currently
    private EdcNotificationRequest toEdcNotificationRequest(
            final NotificationMessage notificationMessage,
            final String senderEdcUrl,
            final EndpointDataReference dataReference,
            final Notification notification
    ) throws JsonProcessingException {

        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(senderEdcUrl, notificationMessage, notification);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String body = objectMapper.writeValueAsString(edcNotification);

        HttpHeaders headers = new HttpHeaders();
        headers.set(Objects.requireNonNull(dataReference.getAuthKey()), dataReference.getAuthCode());
        headers.set("Content-Type", "application/json");
        log.info(":::: Send notificationMessage Data  body :{}, dataReferenceEndpoint :{}", body, dataReference.getEndpoint());
        return EdcNotificationRequest.builder()
                .url(dataReference.getEndpoint())
                .body(body)
                .headers(headers).build();


    }


    private void sendRequest(final EdcNotificationRequest request) {
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
