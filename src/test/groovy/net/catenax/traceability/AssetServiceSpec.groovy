package net.catenax.traceability


import net.catenax.traceability.assets.Asset
import net.catenax.traceability.assets.AssetRepository
import net.catenax.traceability.assets.AssetService
import net.catenax.traceability.assets.QualityType

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
