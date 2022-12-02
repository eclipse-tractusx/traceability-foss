/*
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
 */

package net.catenax.traceability.assets.infrastructure.adapters.openapi.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.FeignException;
import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.infrastructure.adapters.metrics.RegistryLookupMeterRegistry;
import net.catenax.traceability.assets.infrastructure.adapters.metrics.RegistryLookupMetric;
import net.catenax.traceability.assets.infrastructure.adapters.metrics.RegistryLookupMeterRegistry;
import net.catenax.traceability.assets.infrastructure.adapters.metrics.RegistryLookupMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.BATCH_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.MANUFACTURER_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.MANUFACTURER_PART_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.PART_INSTANCE_ID;
import java.util.stream.Collectors;

import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.BATCH_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.MANUFACTURER_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.MANUFACTURER_PART_ID;
import static net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService.AssetIdType.PART_INSTANCE_ID;

@Component
public class RegistryService {

	private static final Logger logger = LoggerFactory.getLogger(RegistryService.class);

	private final ObjectMapper objectMapper;
	private final RegistryApiClient registryApiClient;
	private final String bpn;
	private final String manufacturerIdKey;
	private final RegistryLookupMeterRegistry registryLookupMeterRegistry;
	private final Clock clock;

	enum AssetIdType {
		MANUFACTURER_PART_ID("manufacturerPartId"),
		PART_INSTANCE_ID("partInstanceId"),
		MANUFACTURER_ID("manufacturerId"),
		BATCH_ID("batchId");

		private final String value;

		AssetIdType(String value) {
			this.value = value;
		}

		public String asKey() {
			return this.value.toLowerCase();
		}
	}

	public RegistryService(ObjectMapper objectMapper,
						   RegistryApiClient registryApiClient,
						   @Value("${traceability.bpn}") String bpn,
						   @Value("${traceability.registry.manufacturerIdKey}") String manufacturerIdKey,
						   RegistryLookupMeterRegistry registryLookupMeterRegistry, Clock clock) {
		this.objectMapper = objectMapper;
		this.registryApiClient = registryApiClient;
		this.bpn = bpn;
		this.manufacturerIdKey = manufacturerIdKey;
		this.registryLookupMeterRegistry = registryLookupMeterRegistry;
		this.clock = clock;
	}

	public List<ShellDescriptor> findAssets() {
		RegistryLookupMetric registryLookupMetric = RegistryLookupMetric.start(clock);

		logger.info("Fetching all shell descriptor IDs for BPN {}.", bpn);

		Map<String, Object> filter = new HashMap<>();
		filter.put("assetIds", getFilterValue(manufacturerIdKey, bpn));

		final List<String> assetIds;
		try {
			assetIds = registryApiClient.getAllAssetAdministrationShellIdsByAssetLink(filter);
		} catch (FeignException e) {
			endMetric(registryLookupMetric);

			logger.error("Fetching shell descriptors failed", e);

			throw e;
		}
		logger.info("Received {} shell descriptor IDs.", assetIds.size());

		logger.info("Fetching shell descriptors.");

		final AssetAdministrationShellDescriptorCollectionBase descriptors;
		try {
			descriptors = registryApiClient.postFetchAssetAdministrationShellDescriptor(assetIds);
		} catch (FeignException e) {
			endMetric(registryLookupMetric);

			logger.error("Fetching shell descriptors failed", e);

			throw e;
		}

		logger.info("Received {} shell descriptors for {} IDs.", descriptors.getItems().size(), assetIds.size());

		List<ShellDescriptor> shellDescriptors = descriptors.getItems().stream()
			.filter(it -> Objects.nonNull(it.getGlobalAssetId()))
			.map(aasDescriptor -> toShellDescriptor(aasDescriptor, registryLookupMetric))
			.peek(it -> registryLookupMetric.incrementSuccessShellDescriptorsFetchCount())
			.toList();

		logger.info("Found {} shell descriptors containing a global asset ID.", shellDescriptors.size());

		registryLookupMetric.end(clock);

		registryLookupMeterRegistry.save(registryLookupMetric);

		return shellDescriptors;
	}

	private ShellDescriptor toShellDescriptor(AssetAdministrationShellDescriptor aasDescriptor, RegistryLookupMetric registryLookupMetric) {
		logIncomingDescriptor(aasDescriptor, registryLookupMetric);

		String shellDescriptorId = aasDescriptor.getIdentification();
		String globalAssetId = aasDescriptor.getGlobalAssetId().getValue().stream()
			.findFirst()
			.orElse(null);
		Map<String, String> assetIdsMap = aasDescriptor.getSpecificAssetIds().stream()
			.collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), IdentifierKeyValuePair::getValue));

		String manufacturerPartId = assetIdsMap.get(MANUFACTURER_PART_ID.asKey());
		String partInstanceId = assetIdsMap.get(PART_INSTANCE_ID.asKey());
		String manufacturerId = assetIdsMap.get(MANUFACTURER_ID.asKey());
		String batchId = assetIdsMap.get(BATCH_ID.asKey());

		return new ShellDescriptor(shellDescriptorId, globalAssetId, aasDescriptor.getIdShort(), partInstanceId, manufacturerPartId, manufacturerId, batchId);
	}

	private void endMetric(RegistryLookupMetric registryLookupMetric) {
		registryLookupMetric.incrementFailedShellDescriptorsFetchCount();
		registryLookupMetric.end(clock);

		registryLookupMeterRegistry.save(registryLookupMetric);
	}

	private AssetAdministrationShellDescriptor logIncomingDescriptor(AssetAdministrationShellDescriptor descriptor, RegistryLookupMetric registryLookupMetric) {
		if (logger.isDebugEnabled()) {
			try {
				String rawDescriptor = objectMapper.writeValueAsString(descriptor);
				logger.debug("Received shell descriptor: {}", rawDescriptor);
			} catch (JsonProcessingException e) {
				logger.warn("Failed to write rawDescriptor {} as string", descriptor, e);

				registryLookupMetric.incrementFailedShellDescriptorsFetchCount();
			}
		}
		return descriptor;
	}

	private String getFilterValue(String key, String value) {
		return URLEncoder.encode(String.format("{\"key\":\"%s\",\"value\":\"%s\"}", key, value), StandardCharsets.UTF_8);
	}
}


