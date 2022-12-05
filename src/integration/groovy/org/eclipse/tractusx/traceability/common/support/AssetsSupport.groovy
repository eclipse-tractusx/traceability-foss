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

package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter

trait AssetsSupport implements AssetRepositoryProvider {

	void defaultAssetsStored() {
		assetRepository().saveAll(assetsConverter().readAndConvertAssets())
	}

	void assertAssetsSize(int size) {
		assert assetRepository().countAssets() == size
	}

	void assertHasRequiredIdentifiers() {
		assetRepository().getAssets().each {asset ->
			assert asset.manufacturerId != AssetsConverter.EMPTY_TEXT || asset.batchId != AssetsConverter.EMPTY_TEXT
			assert asset.partInstanceId != AssetsConverter.EMPTY_TEXT || asset.batchId != AssetsConverter.EMPTY_TEXT
			assert asset.idShort != null
		}
	}

	void assertNoAssetsStored() {
		assertAssetsSize(0)
	}
}
