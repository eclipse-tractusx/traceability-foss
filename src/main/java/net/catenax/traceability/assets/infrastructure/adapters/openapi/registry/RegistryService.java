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
import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RegistryService {

	private static final Logger logger = LoggerFactory.getLogger(RegistryService.class);

	private final ObjectMapper objectMapper;
	private final RegistryApiClient registryApiClient;

	public RegistryService(ObjectMapper objectMapper, RegistryApiClient registryApiClient) {
		this.objectMapper = objectMapper;
		this.registryApiClient = registryApiClient;
	}

	public List<ShellDescriptor> findAssetsByBpn(String bpn) {
		logger.info("Fetching all shell descriptor IDs for BPN {}.", bpn);

		Map<String, Object> filter = new HashMap<>();
		filter.put("assetIds", getFilterValue("ManufacturerId", bpn));

		List<String> assetIds = registryApiClient.getAllAssetAdministrationShellIdsByAssetLink(filter);

		logger.info("Received {} shell descriptor IDs.", assetIds.size());

		logger.info("Fetching shell descriptors.");
		AssetAdministrationShellDescriptorCollectionBase descriptors = registryApiClient.postFetchAssetAdministrationShellDescriptor(assetIds);

		logger.info("Received {} shell descriptors for {} IDs.", descriptors.getItems().size(), assetIds.size());

		List<ShellDescriptor> shellDescriptors = descriptors.getItems().stream()
			.filter(it -> it.getGlobalAssetId() != null)
			.map(i -> {
				final String rawDescriptor;

				try {
					rawDescriptor = objectMapper.writeValueAsString(i);
				} catch (JsonProcessingException e) {
					logger.warn("Failed to write rawDescriptor {} as string", i, e);

					throw new IllegalArgumentException(e);
				}

				String globalAssetId = i.getGlobalAssetId().getValue().get(0);

				return new ShellDescriptor(i.getIdentification(), globalAssetId, rawDescriptor);
		}).toList();

		logger.info("Found {} shell descriptors containing a global asset ID.", shellDescriptors.size());

		return shellDescriptors;
	}

	private String getFilterValue(String key, String value) {
		return URLEncoder.encode(String.format("{\"key\":\"%s\",\"value\":\"%s\"}", key, value), StandardCharsets.UTF_8);
	}
}
