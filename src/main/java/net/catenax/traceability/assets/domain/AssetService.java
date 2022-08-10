package net.catenax.traceability.assets.domain;

import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IrsService;
import net.catenax.traceability.assets.infrastructure.adapters.rest.assets.UpdateAsset;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetService {

	private final AssetRepository assetRepository;

	private final IrsService irsService;

	public AssetService(AssetRepository assetRepository, IrsService irsService) {
		this.assetRepository = assetRepository;
		this.irsService = irsService;
	}

	@Async
	public void synchronizeAssets() {
		irsService.synchronizeAssets();
	}

	public Asset updateAsset(String assetId, UpdateAsset updateAsset) {
		Asset foundAsset = assetRepository.getAssetById(assetId);

		if (foundAsset == null) {
			throw new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId));
		}

		Asset updatedAsset = foundAsset.update(updateAsset.qualityType());

		return assetRepository.save(updatedAsset);
	}

	public Map<String, Long> getAssetsCountryMap() {
		return assetRepository.getAssets().stream()
			.collect(Collectors.groupingBy(Asset::manufacturingCountry, Collectors.counting()));
	}
}
