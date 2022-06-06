package net.catenax.traceability.assets;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryAssetsRepository implements AssetRepository {

	private Map<String, Asset> assets = new HashMap<>();

	@PostConstruct
	public void initializeRepository() {
		assets = AssetRandomizer.generateAssets(1000);
	}

	@Override
	public Asset getAssetById(String assetId) {
		return assets.get(assetId);
	}

	@Override
	public Asset getAssetByChildId(String assetId, String childId) {
		return assets.get(assetId).childDescriptions().stream()
			.filter(childDescription -> childDescription.id().equals(childId))
			.map(childDescription -> assets.get(childDescription.id()))
			.findFirst()
			.orElse(null);
	}

	@Override
	public List<Asset> getAssets() {
		return new ArrayList<>(assets.values());
	}

}
