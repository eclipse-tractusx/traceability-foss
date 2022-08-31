package net.catenax.traceability.common.support

import net.catenax.traceability.assets.domain.ports.AssetRepository

interface AssetRepositoryProvider {
	AssetRepository assetRepository()
}
