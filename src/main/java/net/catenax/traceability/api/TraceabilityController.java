package net.catenax.traceability.api;

import net.catenax.traceability.assets.Asset;
import net.catenax.traceability.assets.AssetRepository;
import net.catenax.traceability.assets.AssetService;
import net.catenax.traceability.assets.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class TraceabilityController {

	private final AssetRepository assetRepository;

	private final AssetService assetService;

	public TraceabilityController(AssetRepository assetRepository, AssetService assetService) {
		this.assetRepository = assetRepository;
		this.assetService = assetService;
	}

	@GetMapping("/assets")
	public PageResult<Asset> assets(Pageable pageable) {
		return assetRepository.getAssets(pageable);
	}

	@GetMapping("/assets/{assetId}")
	public Asset asset(@PathVariable String assetId) {
		return assetRepository.getAssetById(assetId);
	}

	@GetMapping("/assets/{assetId}/children/{childId}")
	public Asset asset(@PathVariable String assetId, @PathVariable String childId) {
		return assetRepository.getAssetByChildId(assetId, childId);
	}

	@PatchMapping("/assets/{assetId}")
	public Asset updateAsset(@PathVariable String assetId, @Valid @RequestBody UpdateAsset updateAsset) {
		return assetService.updateAsset(assetId, updateAsset);
	}
}
