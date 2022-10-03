package net.catenax.traceability.common.support

import net.catenax.traceability.assets.domain.ports.AssetRepository
import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter

interface AssetRepositoryProvider {
	AssetRepository assetRepository()

	AssetsConverter assetsConverter()
}
