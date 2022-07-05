package net.catenax.traceability.assets;

import org.springframework.data.domain.Pageable;

public interface AssetRepository {
	Asset getAssetById(String assetId);

	Asset getAssetByChildId(String assetId, String childId);

	PageResult<Asset> getAssets(Pageable pageable);

	Asset save(Asset asset);
}
