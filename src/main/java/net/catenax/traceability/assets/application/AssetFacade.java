package net.catenax.traceability.assets.application;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.service.AssetService;
import net.catenax.traceability.assets.infrastructure.adapters.rest.assets.UpdateAsset;
import net.catenax.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AssetFacade {

	private final AssetService assetService;

	public AssetFacade(AssetService assetService) {
		this.assetService = assetService;
	}

	@Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
	public void synchronizeAssetsAsync(List<String> globalAssetIds) {
		assetService.synchronizeAssets(globalAssetIds);
	}

	public Map<String, Long> getAssetsCountryMap() {
		return assetService.getAssetsCountryMap();
	}

	public Asset updateAsset(String assetId, UpdateAsset updateAsset) {
		return assetService.updateAsset(assetId, updateAsset.qualityType());
	}
}
