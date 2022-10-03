package net.catenax.traceability.common.support

import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter

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
		}
	}

	void assertNoAssetsStored() {
		assertAssetsSize(0)
	}
}
