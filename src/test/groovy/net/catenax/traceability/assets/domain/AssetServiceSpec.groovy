package net.catenax.traceability.assets.domain

import net.catenax.traceability.UnitSpec

class AssetServiceSpec extends UnitSpec {

	AssetRepository repository = Mock()

	AssetService assetService = new AssetService(repository)

	def "should return assets country map"() {
		given:
			repository.getAssets() >> [
				newAsset("DEU"),
				newAsset("DEU"),
				newAsset("DEU"),
				newAsset("POL"),
				newAsset("POL"),
				newAsset("ITA"),
				newAsset("FRA"),
			]

		when:
			Map<String, Long> countryMap = assetService.getAssetsCountryMap()

		then:
			countryMap["DEU"] == 3
			countryMap["POL"] == 2
			countryMap["ITA"] == 1
			countryMap["FRA"] == 1
	}

	Asset newAsset(String country) {
		new Asset(null, null, null, null, null, null, null, null, null, country, QualityType.OK)
	}

}
