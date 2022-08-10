package net.catenax.traceability.assets.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetRepository {
	Asset getAssetById(String assetId);

	Asset getAssetByChildId(String assetId, String childId);

	PageResult<Asset> getAssets(Pageable pageable);

	List<Asset> getAssets();

	Asset save(Asset asset);

	List<Asset> saveAll(List<Asset> assets);

    long countAssets();

    void clean();
}
