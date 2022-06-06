package net.catenax.traceability.api;

import net.catenax.traceability.assets.Asset;
import net.catenax.traceability.assets.AssetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TraceabilityController {

	private AssetRepository assetRepository;

	public TraceabilityController(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	@GetMapping("/assets")
	public List<Asset> assets() {
		return assetRepository.getAssets();
	}

	@GetMapping("/assets/{assetId}")
	public Asset asset(@PathVariable String assetId) {
		return assetRepository.getAssetById(assetId);
	}

	@GetMapping("/assets/{assetId}/children/{childId}")
	public Asset asset(@PathVariable String assetId, @PathVariable String childId) {
		return assetRepository.getAssetByChildId(assetId, childId);
	}
}
