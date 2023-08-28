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
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.catalog.spi.Dataset;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.tractusx.irs.edc.client.ContractNegotiationService;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.exceptions.ContractNegotiationException;
import org.eclipse.tractusx.irs.edc.client.exceptions.TransferProcessException;
import org.eclipse.tractusx.irs.edc.client.exceptions.UsagePolicyException;
import org.eclipse.tractusx.irs.edc.client.model.NegotiationResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.CatalogItem;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.configuration.JsonLdConfigurationTraceX;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationFactory;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.PolicyDefinition;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessDataDestination;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators.AtomicConstraintValidator;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.configuration.JsonLdConfigurationTraceX.NAMESPACE_EDC;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.configuration.JsonLdConfigurationTraceX.NAMESPACE_EDC_ID;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transferprocess.TransferProcessRequest.DEFAULT_PROTOCOL;

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

    @Qualifier("testedc")
    private final EDCCatalogFacade edcCatalogFacade;

    private final ContractNegotiationService contractNegotiationService;

    public static final String ASSET_VALUE_QUALITY_INVESTIGATION = "qualityinvestigation";
    public static final String ASSET_VALUE_QUALITY_ALERT = "qualityalert";
    private static final String ASSET_VALUE_NOTIFICATION_METHOD_UPDATE = "update";
    private static final String ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE = "receive";

    public void startEDCTransfer(QualityNotificationMessage notification, String receiverEdcUrl, String senderEdcUrl) {
        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", edcProperties.getApiAuthKey());
        try {
            notification.setEdcUrl(receiverEdcUrl);

            log.info(":::: Find Notification contract method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}", senderEdcUrl, receiverEdcUrl);

            List<org.eclipse.tractusx.irs.edc.client.model.CatalogItem> catalogItems = null;
            try {
                final String propertyNotificationTypeValue = QualityNotificationType.ALERT.equals(notification.getType()) ? ASSET_VALUE_QUALITY_ALERT : ASSET_VALUE_QUALITY_INVESTIGATION;
                final String propertyMethodValue = notification.getIsInitial() ? ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE : ASSET_VALUE_NOTIFICATION_METHOD_UPDATE;
                catalogItems = edcCatalogFacade.fetchCatalogItems(
                                CatalogRequest.Builder.newInstance()
                                        .protocol(DEFAULT_PROTOCOL)
                                        .providerUrl(receiverEdcUrl + edcProperties.getIdsPath())
                                        .querySpec(QuerySpec.Builder.newInstance()
                                                .filter(
                                                        List.of(new Criterion(NAMESPACE_EDC + "notificationtype", "=", propertyNotificationTypeValue),
                                                                new Criterion(NAMESPACE_EDC + "notificationmethod", "=", propertyMethodValue))
                                                )
                                                .build())
                                        .build()
                        ).stream()
                        .filter(this::hasTracePolicy)
                        .toList();
            } catch (Exception e) {
                log.error(" CATALOG REQUEST LIB", e);
            }

           log.info("CATALOG ITEMS: {}", objectMapper.writeValueAsString(catalogItems));
            Catalog catalog = edcService.getCatalog(
                    senderEdcUrl,
                    receiverEdcUrl + edcProperties.getIdsPath(),
                    header
            );
//            log.info(" CATALOG FOR NOTIFICATION : {}", objectMapper.writeValueAsString(catalog));
//
//            log.info(" DATASET FOR NOTIFICATION : {}", objectMapper.writeValueAsString(catalog.getDatasets()));
            if (catalogItems.isEmpty()) {
                log.info("No CatalogItems found");
                throw new BadRequestException("No catalog items for sending notification.");
            }
//
            Optional<Dataset> filteredDataset = catalog.getDatasets().stream()
                    .filter(dataset -> isQualityNotificationOffer(notification, dataset))
                    .findFirst()
                    .filter(this::hasTracePolicy);


            log.info(":::: Initialize Contract Negotiation method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}", senderEdcUrl, receiverEdcUrl);
            final List<CatalogItem> items = filteredDataset.stream().map(dataSet -> {
                final Map.Entry<String, Policy> offer = dataSet.getOffers()
                        .entrySet()
                        .stream()
                        .findFirst()
                        .orElseThrow();
                final var catalogItem = CatalogItem.builder()
                        .itemId(dataSet.getId())
                        .assetPropId(dataSet.getProperty(NAMESPACE_EDC_ID).toString())
                        .connectorId(catalog.getId())
                        .offerId(offer.getKey())
                        .policy(offer.getValue());
                if (catalog.getProperties().containsKey(JsonLdConfigurationTraceX.NAMESPACE_EDC_PARTICIPANT_ID)) {
                    catalogItem.connectorId(
                            catalog.getProperties().get(JsonLdConfigurationTraceX.NAMESPACE_EDC_PARTICIPANT_ID).toString());
                }

                return catalogItem.build();
            }).toList();

            Optional<org.eclipse.tractusx.irs.edc.client.model.CatalogItem> catalogItem = catalogItems.stream().findFirst();

            if (catalogItem.isEmpty()) {
                log.info("No Catalog Item in catalog found");
                throw new NoCatalogItemException("No Catalog Item in catalog found.");
            }

// TODO
            //NegotiationResponse negotiationResponse = contractNegotiationService.negotiate(null, null);

            //  String contractAgreementId = negotiationResponse.getContractAgreementId();

            NegotiationResponse response ;
            try {
            response = contractNegotiationService.negotiate(receiverEdcUrl, catalogItem.get());
        } catch (TransferProcessException e) {
            throw new RuntimeException(e);
        } catch (UsagePolicyException e) {
            throw new RuntimeException(e);
        } catch (ContractNegotiationException e) {
            throw new RuntimeException(e);
        }
    log.info("LIB contractNegotiation {}", response);

            final String edcContractAgreementId = edcService.initializeContractNegotiation(receiverEdcUrl, items.stream().findFirst().get(), senderEdcUrl, header);

            log.info(":::: Contract Agreed method[startEDCTransfer] agreementId :{}", edcContractAgreementId);

            // TODO remove this because we do not store agreement Id we extract it from a token in EdcCallbackControllerTraceX.java
            endpointDataReferenceCache.storeAgreementId(edcContractAgreementId);

            if (StringUtils.hasLength(edcContractAgreementId)) {
                notification.setContractAgreementId(edcContractAgreementId);
            }

            EndpointDataReference dataReference = endpointDataReferenceCache.get(edcContractAgreementId);
            boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
            if (!validDataReference) {
                log.info(":::: Invalid Data Reference :::::");
                if (dataReference != null) {
                    endpointDataReferenceCache.remove(edcContractAgreementId);
                }

                final TransferProcessRequest transferProcessRequest = createTransferProcessRequest(
                        receiverEdcUrl + edcProperties.getIdsPath(),
                        items.stream().findFirst().get(),
                        edcContractAgreementId);

                log.info(":::: initialize Transfer process with http Proxy :::::");
                // Initiate transfer process
                edcService.initiateHttpProxyTransferProcess(senderEdcUrl,
                        receiverEdcUrl + edcProperties.getIdsPath(),
                        transferProcessRequest,
                        header
                );
                dataReference = getDataReference(edcContractAgreementId);
            }

            Request notificationRequest = buildNotificationRequest(notification, senderEdcUrl, dataReference);

            httpCallService.sendRequest(notificationRequest);

            log.info(":::: EDC Data Transfer Completed :::::");
        } catch (IOException e) {
            throw new BadRequestException("EDC Data Transfer fail.", e);
        } catch (InterruptedException e) {
            log.error("Exception", e);
            Thread.currentThread().interrupt();
        }
    }

    private TransferProcessRequest createTransferProcessRequest(final String providerConnectorUrl,
                                                                final CatalogItem catalogItem,
                                                                final String negotiationId) {
        final var destination = DataAddress.Builder.newInstance()
                .type(TransferProcessDataDestination.DEFAULT_TYPE)
                .build();
        final var transferProcessRequestBuilder = TransferProcessRequest.builder()
                .protocol(
                        TransferProcessRequest.DEFAULT_PROTOCOL)
                .managedResources(
                        TransferProcessRequest.DEFAULT_MANAGED_RESOURCES)
                .connectorId(catalogItem.getConnectorId())
                .connectorAddress(providerConnectorUrl)
                .contractId(negotiationId)
                .assetId(catalogItem.getAssetPropId())
                .dataDestination(destination);

        return transferProcessRequestBuilder.build();
    }

    private Request buildNotificationRequest(QualityNotificationMessage notification, String senderEdcUrl, EndpointDataReference dataReference) throws JsonProcessingException {
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(senderEdcUrl, notification);
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

    @Deprecated
    private boolean isQualityNotificationOffer(QualityNotificationMessage qualityNotificationMessage, Dataset dataset) {
        Object notificationTypeObj = dataset.getProperty(NAMESPACE_EDC + "notificationtype");
        String notificationType = null;
        if (notificationTypeObj != null) {
            notificationType = notificationTypeObj.toString();
        }
        Object notificationMethodObj = dataset.getProperty(NAMESPACE_EDC + "notificationmethod");
        String notificationMethod = null;
        if (notificationMethodObj != null) {
            notificationMethod = notificationMethodObj.toString();
        }

        final String propertyNotificationTypeValue = QualityNotificationType.ALERT.equals(qualityNotificationMessage.getType()) ? ASSET_VALUE_QUALITY_ALERT : ASSET_VALUE_QUALITY_INVESTIGATION;
        final String propertyMethodValue = qualityNotificationMessage.getIsInitial() ? ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE : ASSET_VALUE_NOTIFICATION_METHOD_UPDATE;
        return propertyNotificationTypeValue.equals(notificationType) && propertyMethodValue.equals(notificationMethod);
    }


    private boolean hasTracePolicy(Dataset dataset) {
        boolean foundPolicy = false;
        for (Policy policy : dataset.getOffers().values()) {
            log.info("Policy Check {} ", policy.toString());
            if (!foundPolicy) {
                foundPolicy = isValid(policy);
            }
        }
        log.info("Found policy: {} ", foundPolicy);
        return foundPolicy;
    }

    private boolean hasTracePolicy(org.eclipse.tractusx.irs.edc.client.model.CatalogItem catalogItem) {
        boolean foundPolicy = false;
        if (catalogItem.getPolicy() != null) {
            log.info("Policy Check {} ", catalogItem.getPolicy().toString());
            foundPolicy = isValid(catalogItem.getPolicy());
        }
        log.info("Found policy: {} ", foundPolicy);
        return foundPolicy;
    }


    private List<PolicyDefinition> allowedPolicies() {
        final PolicyDefinition allowedTracePolicy = PolicyDefinition.builder()
                .constraintOperator("EQ")
                .permissionActionType("USE")
                .constraintType("AtomicConstraint")
                .leftExpressionValue("idsc:PURPOSE")
                .rightExpressionValue("ID 3.0 Trace")
                .build();
        return List.of(allowedTracePolicy);
    }


    private boolean isValid(final Policy policy) {
        final List<PolicyDefinition> policyList = this.allowedPolicies();
        return policy.getPermissions()
                .stream()
                .anyMatch(permission -> policyList.stream()
                        .anyMatch(allowedPolicy -> isValid(permission, allowedPolicy)));
    }

    private boolean isValid(final Permission permission, final PolicyDefinition policyDefinition) {
        return permission.getAction().getType().equals(policyDefinition.getPermissionActionType())
                && permission.getConstraints()
                .stream()
                .anyMatch(constraint -> isValid(constraint, policyDefinition));
    }

    private boolean isValid(final Constraint constraint, final PolicyDefinition policyDefinition) {
        if (constraint instanceof AtomicConstraint atomicConstraint) {
            return AtomicConstraintValidator.builder()
                    .atomicConstraint(atomicConstraint)
                    .leftExpressionValue(policyDefinition.getLeftExpressionValue())
                    .rightExpressionValue(policyDefinition.getRightExpressionValue())
                    .expectedOperator(
                            Operator.valueOf(policyDefinition.getConstraintOperator()))
                    .build()
                    .isValid();
        } else if (constraint instanceof OrConstraint orConstraint) {
            return orConstraint.getConstraints()
                    .stream()
                    .anyMatch(constraint1 -> isValid(constraint1, policyDefinition));
        }
        return false;
    }

}
