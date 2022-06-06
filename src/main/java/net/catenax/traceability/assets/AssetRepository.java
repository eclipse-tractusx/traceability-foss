package net.catenax.traceability.assets;

import java.util.List;

public interface AssetRepository {
	Asset getAssetById(String assetId);

	Asset getAssetByChildId(String assetId, String childId);

	List<Asset> getAssets();
}
