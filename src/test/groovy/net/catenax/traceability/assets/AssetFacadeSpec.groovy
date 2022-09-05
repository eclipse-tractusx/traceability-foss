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

package net.catenax.traceability.assets

import net.catenax.traceability.assets.domain.model.Asset
import net.catenax.traceability.assets.domain.model.QualityType

class AssetFacadeSpec extends AssetsSpec {

	def "should return assets country map"() {
		given:
			assetRepository.getAssets() >> [
				newAsset("DEU"),
				newAsset("DEU"),
				newAsset("DEU"),
				newAsset("POL"),
				newAsset("POL"),
				newAsset("ITA"),
				newAsset("FRA"),
			]

		when:
			Map<String, Long> countryMap = assetFacade.getAssetsCountryMap()

		then:
			countryMap["DEU"] == 3
			countryMap["POL"] == 2
			countryMap["ITA"] == 1
			countryMap["FRA"] == 1
	}

	Asset newAsset(String country) {
		new Asset(null, null, null, null, null, null, null, null, null, country, null, QualityType.OK)
	}
}
