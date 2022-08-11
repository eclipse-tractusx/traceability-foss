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

package net.catenax.traceability.assets.domain;

import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IrsService;
import net.catenax.traceability.assets.infrastructure.adapters.rest.assets.UpdateAsset;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetService {

	private final AssetRepository assetRepository;

	private final IrsService irsService;

	public AssetService(AssetRepository assetRepository, IrsService irsService) {
		this.assetRepository = assetRepository;
		this.irsService = irsService;
	}

	@Async
	public void synchronizeAssets() {
		irsService.synchronizeAssets();
	}

	public Asset updateAsset(String assetId, UpdateAsset updateAsset) {
		Asset foundAsset = assetRepository.getAssetById(assetId);

		if (foundAsset == null) {
			throw new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId));
		}

		Asset updatedAsset = foundAsset.update(updateAsset.qualityType());

		return assetRepository.save(updatedAsset);
	}

	public Map<String, Long> getAssetsCountryMap() {
		return assetRepository.getAssets().stream()
			.collect(Collectors.groupingBy(Asset::manufacturingCountry, Collectors.counting()));
	}
}
