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
 *
 ********************************************************************************/
package org.eclipse.tractusx.traceability.notification.domain.contract;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.NotificationType;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.DeleteEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractDefinition;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractDefinitionQuerySpec;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.irs.edc.client.model.EdcTechnicalServiceAuthentication;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.DeleteEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.EdcPolicyDefinitionAlreadyExists;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.config.ApplicationConfig;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationMethod;
import org.eclipse.tractusx.traceability.policies.application.mapper.PolicyMapper;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import policies.response.PolicyResponse;

import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade.CX_TAXO_QUALITY_ALERT_RECEIVE;
import static org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade.CX_TAXO_QUALITY_ALERT_UPDATE;
import static org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade.CX_TAXO_QUALITY_INVESTIGATION_RECEIVE;
import static org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade.CX_TAXO_QUALITY_INVESTIGATION_UPDATE;
import static org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade.DEFAULT_PROTOCOL;

@Slf4j
@Component
@AllArgsConstructor
public class EdcNotificationContractService {

    public static final List<CreateNotificationContractRequest> NOTIFICATION_CONTRACTS = List.of(
            new CreateNotificationContractRequest(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType.QUALITY_ALERT, NotificationMethod.UPDATE),
            new CreateNotificationContractRequest(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType.QUALITY_ALERT, NotificationMethod.RECEIVE),
            new CreateNotificationContractRequest(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType.QUALITY_INVESTIGATION, NotificationMethod.UPDATE),
            new CreateNotificationContractRequest(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType.QUALITY_INVESTIGATION, NotificationMethod.RECEIVE)
    );

    private final EdcAssetService edcAssetService;
    private final EdcPolicyDefinitionService edcPolicyDefinitionService;
    private final EdcContractDefinitionService edcContractDefinitionService;
    private final TraceabilityProperties traceabilityProperties;
    private final PolicyRepository policyRepository;
    private final EDCCatalogFacade edcCatalogFacade;
    private final EdcProperties edcProperties;


    private static final String TRACE_FOSS_QUALITY_NOTIFICATION_INVESTIGATION_URL_TEMPLATE = ApplicationConfig.CONTEXT_PATH + ApplicationConfig.INTERNAL_ENDPOINT + "/qualitynotifications/%s";
    private static final String TRACE_FOSS_QUALITY_NOTIFICATION_ALERT_URL_TEMPLATE = ApplicationConfig.CONTEXT_PATH + ApplicationConfig.INTERNAL_ENDPOINT + "/qualityalerts/%s";

    private static EdcContractDefinitionQuerySpec getEdcContractDefinitionQuerySpec(List<String> negotiationCatalogOfferIds) {
        EdcContractDefinitionQuerySpec.FilterExpression filterExpression = EdcContractDefinitionQuerySpec
                .FilterExpression.builder()
                .operandLeft("assetsSelector.operandRight")
                .operator("in")
                .operandRight(negotiationCatalogOfferIds)
                .build();

        return EdcContractDefinitionQuerySpec.builder()
                .edcContext(new EdcContractDefinitionQuerySpec.EdcContext())
                .filterExpression(List.of(filterExpression))
                .build();

    }

    private void revertAccessPolicy(String accessPolicyId) {
        log.info("Removing {} access policy", accessPolicyId);

        try {
            edcPolicyDefinitionService.deleteAccessPolicy(accessPolicyId);
        } catch (DeleteEdcPolicyDefinitionException e) {
            throw new CreateNotificationContractException(e);
        }
    }

    private void revertNotificationAsset(String notificationAssetId) {
        log.info("Removing {} notification asset", notificationAssetId);

        try {
            edcAssetService.deleteAsset(notificationAssetId);
        } catch (DeleteEdcAssetException e) {
            throw new CreateNotificationContractException(e);
        }
    }

    private String createBaseUrl(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType notificationType, NotificationMethod notificationMethod) {
        final String template = notificationType.equals(org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType.QUALITY_ALERT) ? TRACE_FOSS_QUALITY_NOTIFICATION_ALERT_URL_TEMPLATE : TRACE_FOSS_QUALITY_NOTIFICATION_INVESTIGATION_URL_TEMPLATE;
        return traceabilityProperties.getInternalUrl() + template.formatted(notificationMethod.getValue());
    }

    public CreateNotificationContractResponse createNotificationContract(CreateNotificationContractRequest request) {

        NotificationMethod notificationMethod = request.notificationMethod();

        log.info("Creating EDC asset notification contract for {} method and {} notification type", notificationMethod.getValue(), request.notificationType().getValue());

        String notificationAssetId;
        final EdcTechnicalServiceAuthentication edcTechnicalServiceAuthentication =
                EdcTechnicalServiceAuthentication.builder().technicalServiceApiKey(traceabilityProperties.getTechnicalServiceApiKey()).build();
        try {
            notificationAssetId = edcAssetService.createNotificationAsset(
                    createBaseUrl(request.notificationType(), request.notificationMethod()),
                    request.notificationType().name() + " " + request.notificationMethod().name(),
                    org.eclipse.tractusx.irs.edc.client.asset.model.NotificationMethod.valueOf(request.notificationMethod().name()),
                    NotificationType.valueOf(request.notificationType().name()), edcTechnicalServiceAuthentication);
        } catch (CreateEdcAssetException e) {
            throw new CreateNotificationContractException(e);
        }


        Optional<PolicyResponse> optionalPolicyResponse = policyRepository.getNewestPolicyByOwnBpn();
        EdcCreatePolicyDefinitionRequest edcCreatePolicyDefinitionRequest;
        if (optionalPolicyResponse.isPresent()) {
            edcCreatePolicyDefinitionRequest =
                    PolicyMapper
                            .mapToEdcPolicyRequest(optionalPolicyResponse.get());
        } else {
            throw new CreateNotificationContractException("Could not find a policy within IRS Policy store, that matches the own BPN " + traceabilityProperties.getBpn().value() + " or a default policy.");
        }


        String accessPolicyId = "";

        try {
            boolean exists = edcPolicyDefinitionService.policyDefinitionExists(edcCreatePolicyDefinitionRequest.getPolicyDefinitionId());
            if (exists) {
                log.info("Policy with id " + edcCreatePolicyDefinitionRequest.getPolicyDefinitionId() + "already exists and contains necessary application constraints. Reusing for notification contract.");
                accessPolicyId = edcCreatePolicyDefinitionRequest.getPolicyDefinitionId();
            } else {
                accessPolicyId = edcPolicyDefinitionService.createAccessPolicy(edcCreatePolicyDefinitionRequest);
            }
        } catch (CreateEdcPolicyDefinitionException e) {
            revertNotificationAsset(notificationAssetId);
            throw new CreateNotificationContractException(e);
        } catch (EdcPolicyDefinitionAlreadyExists alreadyExists) {
            accessPolicyId = optionalPolicyResponse.get().policyId();
            log.info("Policy with id " + accessPolicyId + " already exists, using for notification contract.");
        } catch (
                org.eclipse.tractusx.irs.edc.client.policy.model.exception.GetEdcPolicyDefinitionException edcPolicyDefinitionException) {
            log.warn("EdcPolicyDefinition could not be queried {}", edcPolicyDefinitionException.getMessage());
        }
        String contractDefinitionId;
        try {
            contractDefinitionId = edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            revertAccessPolicy(accessPolicyId);
            revertNotificationAsset(notificationAssetId);

            throw new CreateNotificationContractException(e);
        }

        log.info(
                "Created notification contract for {} notification asset id, access policy id {} and contract definition id {}",
                notificationAssetId,
                accessPolicyId,
                contractDefinitionId);

        return new CreateNotificationContractResponse(
                notificationAssetId,
                accessPolicyId,
                contractDefinitionId
        );
    }

    public void updateNotificationContractDefinitions() {
        deleteNotificationContracts();
        NOTIFICATION_CONTRACTS.forEach(this::createNotificationContract);
    }

    private void deleteNotificationContracts() {
        log.info("Start deletion of existing notification offers...");
        //get catalog with notification offers
        log.info("Try to fetch existing notification catalog offers with QuerySpec: {}", catalogRequest().getQuerySpec());
        List<CatalogItem> catalogItems = edcCatalogFacade.fetchCatalogItems(catalogRequest());
        log.info("Received notification catalog offers: {}", catalogItems);

        if (CollectionUtils.isEmpty(catalogItems)) {
            log.info("No catalog offers received, cancelling deletion of catalog offers.");
            return;
        }

        //get contract definitions that include the notification offers
        EdcContractDefinitionQuerySpec edcContractDefinitionQuerySpec = getEdcContractDefinitionQuerySpec(catalogItems.stream().map(CatalogItem::getItemId).toList());
        log.info("Try to fetch existing contract definitions with QuerySpec: {}", edcContractDefinitionQuerySpec);
        ResponseEntity<List<EdcContractDefinition>> contractDefinitions = edcContractDefinitionService.getContractDefinitions(edcContractDefinitionQuerySpec);
        List<String> contractDefinitionIds = CollectionUtils.emptyIfNull(contractDefinitions.getBody()).stream().map(EdcContractDefinition::getContractDefinitionId).toList();
        log.info("Received contract definition Ids: {}", contractDefinitionIds);

        //delete contract definitions by id
        log.info("Try to delete contract definitions");
        contractDefinitionIds.forEach(edcContractDefinitionService::deleteContractDefinition);
        log.info("Successfully deleted existing notification offers");
    }

    private CatalogRequest catalogRequest() {
        return CatalogRequest.Builder.newInstance()
                .protocol(DEFAULT_PROTOCOL)
                .counterPartyAddress(edcProperties.getProviderEdcUrl() + edcProperties.getIdsPath())
                .counterPartyId(traceabilityProperties.getBpn().value())
                .querySpec(QuerySpec.Builder.newInstance()
                        .filter(
                                List.of(new Criterion("'http://purl.org/dc/terms/type'.'@id'", "in", List.of(
                                        CX_TAXO_QUALITY_INVESTIGATION_RECEIVE,
                                        CX_TAXO_QUALITY_INVESTIGATION_UPDATE,
                                        CX_TAXO_QUALITY_ALERT_RECEIVE,
                                        CX_TAXO_QUALITY_ALERT_UPDATE
                                )))
                        )
                        .build())
                .build();
    }

}
