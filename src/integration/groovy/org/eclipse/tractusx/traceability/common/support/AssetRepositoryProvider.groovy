package org.eclipse.tractusx.traceability.common.support


import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter

interface AssetRepositoryProvider {
	AssetRepository assetRepository()

    AssetsConverter assetsConverter()
}
