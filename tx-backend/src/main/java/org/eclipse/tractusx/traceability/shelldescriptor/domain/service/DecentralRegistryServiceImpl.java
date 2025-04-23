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

package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import static org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType.PART_INSTANCE;
import static org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType.PART_TYPE;

import assets.request.PartChainIdentificationKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orders.request.CreateOrderRequest;
import orders.request.CreateOrderResponse;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order.OrderBuilder;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.DigitalTwinPartNotFoundException;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.exception.BpnDoesNotMatchException;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.exception.NotOwnPartException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Component
public class DecentralRegistryServiceImpl implements DecentralRegistryService {

    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;
    private final OrderService orderService;
    private final TraceabilityProperties traceabilityProperties;

    @Override
    @Transactional
    public void registerOrdersForExpiredAssets(OrderConfiguration orderConfiguration) {
        List<AssetBase> assetsAsBuiltToSynchronize = assetAsBuiltRepository.findAllExpired();
        log.info("Found {} assets as built to synchronize", assetsAsBuiltToSynchronize.size());
        List<String> assetsAsBuiltIds = assetsAsBuiltToSynchronize.stream().map(AssetBase::getId).toList();

        List<AssetBase> assetsAsPlannedToSynchronize = assetAsPlannedRepository.findAllExpired();
        log.info("Found {} assets as planned to synchronize", assetsAsPlannedToSynchronize.size());
        List<String> assetsAsPlannedIds = assetsAsPlannedToSynchronize.stream().map(AssetBase::getId).toList();

        List<PartChainIdentificationKey> assetAsBuildKeys = assetsAsBuiltIds.stream()
                .map(id -> new PartChainIdentificationKey(null, id, traceabilityProperties.getBpn().toString()))
                .toList();

        CreateOrderResponse createOrderResponseAsBuilt = assetAsBuiltService.syncAssetsUsingIRSOrderAPI(assetAsBuildKeys,
                orderConfiguration);

        List<PartChainIdentificationKey> assetAsPlannedKeys = assetsAsPlannedIds.stream()
                .map(id -> new PartChainIdentificationKey(null, id, traceabilityProperties.getBpn().toString()))
                .toList();

        CreateOrderResponse createOrderResponseAsPlanned = assetAsBuiltService.syncAssetsUsingIRSOrderAPI(assetAsPlannedKeys,
                orderConfiguration);

        createOrderResponseAsBuilt.orderIds().forEach(orderId -> persistOrder(orderId, orderConfiguration, assetsAsBuiltToSynchronize, assetAsBuiltRepository::saveAll));
        createOrderResponseAsPlanned.orderIds().forEach(orderId -> persistOrder(orderId, orderConfiguration, assetsAsPlannedToSynchronize, assetAsPlannedRepository::saveAll));
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest, OrderConfiguration orderConfiguration) {
        validatePartsOwnership(createOrderRequest.keys());

        Map<String, String> globalAssetIdsBpns = createOrderRequest.keys().stream()
                .filter(key -> key.globalAssetId() != null)
                .collect(Collectors.toMap(
                        PartChainIdentificationKey::globalAssetId,
                        PartChainIdentificationKey::bpn
                ));

        Map<String, String> aasIdentifiers = createOrderRequest.keys().stream()
                .filter(key -> key.identifier() != null)
                .collect(Collectors.toMap(
                        PartChainIdentificationKey::identifier,
                        PartChainIdentificationKey::bpn
                ));

        List<PartChainIdentificationKey> globalAssetIdKeys = globalAssetIdsBpns.entrySet().stream()
                .map(entry -> new PartChainIdentificationKey(null, entry.getKey(), entry.getValue()))
                .toList();

        List<PartChainIdentificationKey> aasIdentifiersKeys = aasIdentifiers.entrySet().stream()
                .map(entry -> new PartChainIdentificationKey(entry.getKey(), null, entry.getValue()))
                .toList();

        List<PartChainIdentificationKey> identifiers = Stream
                .concat(globalAssetIdKeys.stream(), aasIdentifiersKeys.stream()).toList();

        if (!identifiers.isEmpty()) {
            switch (DigitalTwinType.digitalTwinTypeFromString(createOrderRequest.digitalTwinType())) {
                case PART_INSTANCE -> {
                    List<AssetBase> assetBases = getOrInsertByGlobalAssetId(globalAssetIdsBpns, assetAsBuiltRepository,
                            PART_INSTANCE);

                    CreateOrderResponse createOrderResponse = assetAsBuiltService
                            .syncAssetsUsingIRSOrderAPI(identifiers, orderConfiguration);

                    createOrderResponse.orderIds().forEach(
                            orderId -> persistOrder(orderId, orderConfiguration, assetBases,
                                    assetAsBuiltRepository::saveAll));

                    return createOrderResponse;
                }
                case PART_TYPE -> {
                    List<AssetBase> assetBases = getOrInsertByGlobalAssetId(globalAssetIdsBpns,
                            assetAsPlannedRepository, PART_TYPE);

                    CreateOrderResponse createOrderResponse = assetAsPlannedService
                            .syncAssetsUsingIRSOrderAPI(identifiers, orderConfiguration);

                    createOrderResponse.orderIds().forEach(
                            orderId -> persistOrder(orderId, orderConfiguration, assetBases,
                                    assetAsPlannedRepository::saveAll));

                    return createOrderResponse;
                }
            }

            throw new DigitalTwinPartNotFoundException(createOrderRequest.digitalTwinType());
        } else {
            log.warn("No global asset IDs or AAS identifiers found in the request");
            return null;
        }
    }

    private void validatePartsOwnership(List<PartChainIdentificationKey> keys) {
        for (PartChainIdentificationKey key : keys) {
            if (!traceabilityProperties.getBpn().value().equals(key.bpn())) {
                throw new NotOwnPartException(key.globalAssetId(), traceabilityProperties.getBpn().value());
            }
        }
    }

    private void persistOrder(String orderId, OrderConfiguration orderConfiguration,
            List<AssetBase> assetBases, Consumer<List<AssetBase>> repository) {
        if (!StringUtils.isEmpty(orderId)) {
            log.info("Order created: {}", orderId);

            OrderBuilder orderBuilder = Order.builder()
                    .id(orderId)
                    .orderConfiguration(orderConfiguration)
                    .status(ProcessingState.INITIALIZED);

            orderBuilder.partsAsBuilt(assetBases.stream()
                    .filter(assetBase -> PART_INSTANCE.getValue().equalsIgnoreCase(assetBase.getDigitalTwinType()))
                    .collect(Collectors.toSet()));

            orderBuilder.partsAsPlanned(assetBases.stream()
                    .filter(assetBase -> PART_TYPE.getValue().equalsIgnoreCase(assetBase.getDigitalTwinType()))
                    .collect(Collectors.toSet()));

            orderService.persistOrder(orderBuilder.build());

            log.info("Changing assets import state to IN_SYNCHRONIZATION after creating an order");
            updateAssetsStatus(assetBases, repository);
        } else {
            log.warn("No order created, skipping assets synchronization");
        }
    }

    private List<AssetBase> getOrInsertByGlobalAssetId(
            Map<String, String> globalAssetIds,
            AssetRepository repository,
            DigitalTwinType digitalTwinType) {
        List<AssetBase> existingAssets = new ArrayList<>();
        Map<String, String> newAssetIds = new HashMap<>();
        globalAssetIds.forEach((globalAssetId, bpn) -> repository.findById(globalAssetId).ifPresentOrElse(assetBase -> {
            if (!assetBase.getManufacturerId().equalsIgnoreCase(bpn)) {
                throw new BpnDoesNotMatchException(bpn, assetBase.getManufacturerId());
            }
            existingAssets.add(assetBase);
        }, () -> {
            log.info("Asset with id {} not found, creating new one", globalAssetId);
            newAssetIds.put(globalAssetId, bpn);
        }));

        List<AssetBase> savedAssetBases = createNewAssets(newAssetIds, digitalTwinType, repository);

        existingAssets.addAll(savedAssetBases);
        return existingAssets;
    }

    private List<AssetBase> createNewAssets(Map<String, String> newAssetIds, DigitalTwinType digitalTwinType, AssetRepository repository) {
        List<AssetBase> toInsert = newAssetIds.entrySet().stream().map(entry -> AssetBase.builder()
                        .importState(ImportState.NEW)
                        .id(entry.getKey())
                        .owner(Owner.OWN)
                        .manufacturerId(entry.getValue())
                        .digitalTwinType(digitalTwinType.getValue())
                        .build())
                .peek(assetBase -> log.info("Creating new asset: [globalAssetId: {}, bpn: {}]", assetBase.getId(), assetBase.getManufacturerId()))
                .toList();
        return repository.saveAll(toInsert);
    }

    private void updateAssetsStatus(List<AssetBase> assets, Consumer<List<AssetBase>> repository) {
        assets.forEach(assetBase -> {
            assetBase.setImportState(ImportState.IN_SYNCHRONIZATION);
            assetBase.setImportNote(ImportNote.IN_SYNCHRONIZATION);
        });
        repository.accept(assets);
    }
}
