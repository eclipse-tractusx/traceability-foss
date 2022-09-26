package net.catenax.traceability.common.support

import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter

trait AssetsSupport implements AssetRepositoryProvider {

	private static final AssetsConverter CONVERTER = new AssetsConverter()

	void defaultAssetsStored() {
		assetRepository().saveAll(CONVERTER.readAndConvertAssets())
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
