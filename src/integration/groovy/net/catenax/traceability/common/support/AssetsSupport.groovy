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

	void assertNoAssetsStored() {
		assertAssetsSize(0)
	}
}
