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

package org.eclipse.tractusx.traceability.assets.application;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest.assets.UpdateAsset;
import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AssetFacade {

	private final AssetService assetService;

	public AssetFacade(AssetService assetService) {
		this.assetService = assetService;
	}

	@Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
	public void synchronizeAssetsAsync(List<String> globalAssetIds) {
		assetService.synchronizeAssets(globalAssetIds);
	}


	public Map<String, Long> getAssetsCountryMap() {
		return assetService.getAssetsCountryMap();
	}

	public Asset updateAsset(String assetId, UpdateAsset updateAsset) {
		return assetService.updateQualityType(assetId, updateAsset.qualityType());
	}

}
