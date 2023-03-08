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

package org.eclipse.tractusx.traceability.assets;


import org.eclipse.tractusx.traceability.assets.application.AssetFacade;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetFacadeTest {

	@InjectMocks
	private AssetFacade assetFacade;

	@Mock
	AssetService assetService;

	@Mock
	private AssetRepository assetRepository;

	@Test
	@DisplayName("get AssetsCountryMap")
	void testGetAssetsCountryMap() {

		// given
		List<Asset> assets = Arrays.asList(
			newAsset("DEU"),
			newAsset("DEU"),
			newAsset("DEU"),
			newAsset("POL"),
			newAsset("POL"),
			newAsset("ITA"),
			newAsset("FRA")
		);


		Map<String,Long> assetMap = new HashMap<>();


		for(Asset asset : assets) {
			assetMap.put(asset.getManufacturingCountry(),0L);
		}


		when(assetService.getAssetsCountryMap()).thenReturn(getCountryCodesWithCountOfOccurrence(assets));

		// when
		Map<String, Long> countryMap = assetFacade.getAssetsCountryMap();

		// then
		assertEquals(3, countryMap.get("DEU"));
		assertEquals(2, countryMap.get("POL"));
		assertEquals(1, countryMap.get("ITA"));
		assertEquals(1, countryMap.get("FRA"));
	}

	// util functions
	private Asset newAsset(String country) {
		return new Asset(
			null, null, null, null, null, null, null, null, null, null, null,
			country, true, null, false, QualityType.OK, null
		);
	}

	private Map<String,Long> getCountryCodesWithCountOfOccurrence(List<Asset> assets) {

		Map<String, Long> occurrences = new HashMap<>();

		for(Asset asset : assets) {
			String countryCode = asset.getManufacturingCountry();
			if(occurrences.get(countryCode) == null) {
				occurrences.put(countryCode, 1L);
			} else {
				occurrences.put(countryCode, Long.sum(occurrences.get(countryCode), 1L));
			}

		}

		return occurrences;
	}
}
