/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.PartChainIdentificationKey;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterOrderRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsBatchResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.JobState;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationJPARepository;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_PLANNED;
import static org.eclipse.tractusx.traceability.common.security.Sanitizer.sanitize;

@Slf4j
@Service
public class OrderRepositoryImpl implements OrderRepository {

    private final TraceabilityProperties traceabilityProperties;
    private final AssetCallbackRepository assetAsBuiltCallbackRepository;
    private final AssetCallbackRepository assetAsPlannedCallbackRepository;
    private static final ProcessingState STATUS_COMPLETED = ProcessingState.COMPLETED;
    private static final ProcessingState STATUS_PARTIAL = ProcessingState.PARTIAL;
    private static final JobState JOB_STATUS_COMPLETED = JobState.COMPLETED;
    private final IrsResponseAssetMapper assetMapperFactory;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;
    private final JobClient jobClient;
    private final OrderJPARepository orderJPARepository;
    private final TriggerConfigurationJPARepository triggerConfigurationJPARepository;

    public OrderRepositoryImpl(
            OrderClient orderClient,
            TraceabilityProperties traceabilityProperties,
            @Qualifier("assetAsBuiltRepositoryImpl")
            AssetCallbackRepository assetAsBuiltCallbackRepository,
            @Qualifier("assetAsPlannedRepositoryImpl")
            AssetCallbackRepository assetAsPlannedCallbackRepository,
            IrsResponseAssetMapper assetMapperFactory,
            ObjectMapper objectMapper,
            JobClient jobClient,
            OrderJPARepository orderJPARepository,
            TriggerConfigurationJPARepository triggerConfigurationJPARepository) {
        this.traceabilityProperties = traceabilityProperties;
        this.assetAsBuiltCallbackRepository = assetAsBuiltCallbackRepository;
        this.assetAsPlannedCallbackRepository = assetAsPlannedCallbackRepository;
        this.orderClient = orderClient;
        this.assetMapperFactory = assetMapperFactory;
        this.objectMapper = objectMapper;
        this.jobClient = jobClient;
        this.orderJPARepository = orderJPARepository;
        this.triggerConfigurationJPARepository = triggerConfigurationJPARepository;
    }

    @Override
    public String createOrderToResolveAssets(
            List<String> ids,
            Direction direction,
            List<String> aspects,
            BomLifecycle bomLifecycle,
            OrderConfiguration orderConfiguration) {
        final String callbackUrl = traceabilityProperties.getUrl();
        String applicationBpn = traceabilityProperties.getBpn().toString();
        List<PartChainIdentificationKey> keys = ids.stream().map(id -> new PartChainIdentificationKey(null, id, applicationBpn)).collect(Collectors.toList());
        RegisterOrderRequest registerOrderRequest = RegisterOrderRequest.buildOrderRequest(aspects, bomLifecycle, callbackUrl, direction, keys, orderConfiguration);
        return orderClient.registerOrder(registerOrderRequest).id();
    }

    @Override
    @Transactional
    public void handleOrderFinishedCallback(String orderId, String batchId, ProcessingState orderState, ProcessingState batchState) {
        log.info("Handling order finished callback for orderId: {}, batchId: {}, orderState: {}, batchState: {}",
                sanitize(orderId), sanitize(batchId), orderState, batchState);

        updateOrderTable(orderId, orderState);

        if (!STATUS_COMPLETED.equals(batchState) && (!STATUS_PARTIAL.equals(batchState))) {
            log.info("Skipping callback handling for orderId: {}, batchId: {} because batchState is (actual: {}).",
                    sanitize(orderId), sanitize(batchId), batchState);
            return;
        }

        final IrsBatchResponse irsBatchResponse = this.orderClient.getBatchByOrder(orderId, batchId);

        if (irsBatchResponse == null) {
            log.warn("Received null response for batch details from IRS for orderId: {}, batchId: {}.",
                    sanitize(orderId), sanitize(batchId));
            return;
        }

        if (irsBatchResponse.jobs().isEmpty()) {
            log.warn("IRS batch response for orderId: {}, batchId: {} contains no jobs.", sanitize(orderId), sanitize(batchId));
            return;
        }

        log.info("Processing {} jobs in IRS batch response for orderId: {}, batchId: {}.",
                irsBatchResponse.jobs().size(), sanitize(orderId), sanitize(batchId));

        irsBatchResponse.jobs().forEach(jobRecord -> {
            log.debug("Processing job with ID: {} for orderId: {}, batchId: {}.", jobRecord.id(), sanitize(orderId), sanitize(batchId));
            IRSResponse irsJobDetailResponse = jobClient.getIrsJobDetailResponse(jobRecord.id());

            if (irsJobDetailResponse == null) {
                log.warn("Skipping job with ID: {} as IRS job detail response is null.", jobRecord.id());
                return;
            }
            if (irsJobDetailResponse.jobStatus() == null || irsJobDetailResponse.jobStatus().state() == null) {
                log.warn("Skipping job with ID: {} as IRS job status or its state is null.", jobRecord.id());
                return;
            }
            if (STATUS_PARTIAL.equals(batchState)) {
                JobState jobState = JobState.fromString(irsJobDetailResponse.jobStatus().state());
                if (!JOB_STATUS_COMPLETED.equals(jobState)) {
                    log.info("Skipping job with ID: {} in PARTIAL batch as it is not COMPLETED (actual: {}).",
                            jobRecord.id(), irsJobDetailResponse.jobStatus().state());
                    return;
                }
            }
            List<AssetBase> assets = assetMapperFactory.toAssetBaseList(irsJobDetailResponse);

            assets.forEach(assetBase -> {
                addTombstoneDetails(assetBase);
                if (assetBase.getBomLifecycle() == AS_BUILT) {
                    saveOrUpdateAssets(assetAsBuiltCallbackRepository, assetBase);
                } else if (assetBase.getBomLifecycle() == AS_PLANNED) {
                    saveOrUpdateAssets(assetAsPlannedCallbackRepository, assetBase);
                }
            });
        });

    }

    private void updateOrderTable(String orderId, ProcessingState orderState) {
        Optional<OrderEntity> order = orderJPARepository.findById(orderId);

        if (order.isPresent()) {
            OrderEntity orderEntity = order.get();
            orderEntity.setStatus(orderState);
            orderJPARepository.save(orderEntity);
        } else {
            log.warn("Order with ID: {} not found in the database.", orderId);
        }
    }

    void addTombstoneDetails(AssetBase tombstone) {
        if (tombstone.getSemanticDataModel().equals(SemanticDataModel.TOMBSTONEASBUILT)) {
            assetAsBuiltCallbackRepository
                    .findById(tombstone.getId())
                    .ifPresent(oldAsset -> {
                        tombstone.setManufacturerId(oldAsset.getManufacturerId());
                        if (oldAsset.getOwner() != null && tombstone.getOwner() == Owner.UNKNOWN) {
                            tombstone.setOwner(oldAsset.getOwner());
                        }
                    });
        } else if (tombstone.getSemanticDataModel().equals(SemanticDataModel.TOMBSTONEASPLANNED)) {
            assetAsPlannedCallbackRepository
                    .findById(tombstone.getId())
                    .ifPresent(oldAsset -> {
                        tombstone.setManufacturerId(oldAsset.getManufacturerId());
                        if (oldAsset.getOwner() != null && tombstone.getOwner() == Owner.UNKNOWN) {
                            tombstone.setOwner(oldAsset.getOwner());
                        }
                    });
        }
    }

    void saveOrUpdateAssets(AssetCallbackRepository repository, AssetBase asset) {
        try {
            enrichAssetBaseByContractAgreements(repository, asset);
            updateExpirationDate(repository, asset);
            repository.save(asset);
        } catch (DataIntegrityViolationException ex) {
            //retry save in case of ERROR: duplicate key value violates unique constraint "asset_pkey"
            log.info("Asset with id {} already exists in the database. The record will be updated instead.", asset.getId());
            repository.save(asset);
        }
    }

    private void updateExpirationDate(AssetCallbackRepository repository, AssetBase asset) {
        triggerConfigurationJPARepository.findTopByCreatedAtDesc().ifPresentOrElse(triggerConfiguration -> {
            Optional<AssetBase> assetToUpdate = repository.findById(asset.getId());
            assetToUpdate.ifPresentOrElse(assetBase -> {
                if (assetBase.getImportState() != ImportState.ERROR) {
                    asset.setTtl(triggerConfiguration.getAasTTL());
                    asset.setExpirationDate(LocalDateTime.now().plusSeconds(triggerConfiguration.getAasTTL() / 1000));
                } else {
                    log.info("Asset with id {} has an import state of ERROR, skipping updating assets expiration date", asset.getId());
                }
            }, () -> log.info("No asset found with id {}, skipping updating assets expiration date", asset.getId()));
        }, () -> log.info("No trigger configuration found, skipping updating assets expiration date"));
    }

    private void enrichAssetBaseByContractAgreements(AssetCallbackRepository repository, AssetBase asset) {
        Optional<AssetBase> byId = repository.findById(asset.getId());
        List<ContractAgreement> agreementsToAdd = new ArrayList<>();
        byId.ifPresent(assetBase -> agreementsToAdd.addAll(assetBase.getContractAgreements()));
        try {
            log.info("Found the following existing contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContractType contractType = asset.getSemanticDataModel().isAsBuilt() ? ContractType.ASSET_AS_BUILT : ContractType.ASSET_AS_PLANNED;
        if (asset.getLatestContractAgreementId() != null) {
            agreementsToAdd.add(ContractAgreement.toDomain(asset.getLatestContractAgreementId(), asset.getId(), contractType));
        }
        asset.setContractAgreements(agreementsToAdd);
        try {
            log.info("Found the following NEW contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Order order) {
        orderJPARepository
                .save(OrderEntity.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .orderConfigurationId(order.getOrderConfiguration() != null ? order.getOrderConfiguration().getId() : null)
                        .message(order.getMessage())
                        .build());
    }

}
