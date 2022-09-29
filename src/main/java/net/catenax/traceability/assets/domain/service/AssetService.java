/********************************************************************************
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
 ********************************************************************************/

package net.catenax.traceability.assets.domain.service;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.AssetNotFoundException;
import net.catenax.traceability.assets.domain.model.QualityType;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.assets.domain.ports.IrsRepository;
import net.catenax.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetService {
	private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

	private final AssetRepository assetRepository;
	private final IrsRepository irsRepository;

	public AssetService(AssetRepository assetRepository, IrsRepository irsRepository) {
		this.assetRepository = assetRepository;
		this.irsRepository = irsRepository;
	}

	public void synchronizeAssets(List<String> globalAssetIds) {
		for (String globalAssetId : globalAssetIds) {
			try {
				synchronizeAssets(globalAssetId);
			} catch (Exception e) {
				logger.warn("Cannot fetch assets for id: {}. Error: {}", globalAssetId, e.getMessage());
			}
		}
	}

	@Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
	public void synchronizeAssets(String globalAssetId) {
		logger.info("Synchronizing assets for globalAssetId: {}", globalAssetId);

		try {
			List<Asset> assets = irsRepository.findAssets(globalAssetId);

			logger.info("Assets synchronization for globalAssetId: {} is done. Found {} assets. Saving them in the repository.", globalAssetId, assets.size());

			assetRepository.saveAll(assets);

			logger.info("Assets for globalAssetId {} successfully saved.", globalAssetId);
		} catch (Exception e) {
			logger.warn("Exception during assets synchronization for globalAssetId: {}. Message: {}.", globalAssetId, e.getMessage(), e);
		}
	}

	public Asset updateAsset(String assetId, QualityType qualityType) {
		Asset foundAsset = assetRepository.getAssetById(assetId);

		if (foundAsset == null) {
			throw new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId));
		}

		foundAsset.updateQualityType(qualityType);

		return assetRepository.save(foundAsset);
	}

	public void startInvestigation(List<String> assetIds, String description) {
		assetRepository.startInvestigation(assetIds, description);
	}

	public Map<String, Long> getAssetsCountryMap() {
		return assetRepository.getAssets().stream()
			.collect(Collectors.groupingBy(Asset::getManufacturingCountry, Collectors.counting()));
	}
}
