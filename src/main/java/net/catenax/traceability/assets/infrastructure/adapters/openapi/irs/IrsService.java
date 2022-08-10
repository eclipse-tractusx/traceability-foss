package net.catenax.traceability.assets.infrastructure.adapters.openapi.irs;

import net.catenax.traceability.assets.domain.Asset;
import net.catenax.traceability.assets.domain.AssetRepository;
import net.catenax.traceability.assets.domain.AssetsConverter;
import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IRSApiClient.JobResponse;
import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IRSApiClient.StartJobRequest;
import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IRSApiClient.StartJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IrsService {

	private static final Logger logger = LoggerFactory.getLogger(IrsService.class);

	private IRSApiClient irsClient;

	private AssetRepository assetRepository;

	private String globalAssetId;

	public IrsService(IRSApiClient irsClient, AssetRepository assetRepository, @Value("${feign.irsApi.globalAssetId}") String globalAssetId) {
		this.irsClient = irsClient;
		this.assetRepository = assetRepository;
		this.globalAssetId = globalAssetId;
	}

	public void synchronizeAssets() {
		logger.info("Synchronizing assets for globalAssetId: {}", globalAssetId);

		AssetsConverter assetsConverter = new AssetsConverter();
		StartJobResponse job = irsClient.registerJob(StartJobRequest.forGlobalAssetId(globalAssetId));
		JobResponse jobDetails = irsClient.getJobDetails(job.jobId());
		List<Asset> assets = assetsConverter.convertAssets(jobDetails);

		if (assets.isEmpty()) { // fallback method just in case, remove after demo
			logger.warn("Submodels not found in job details. Using defaults...");
			assets = assetsConverter.readAndConvertAssets();
		}

		logger.info("Assets synchronization for globalAssetId: {} COMPLETED. Found {} assets.", globalAssetId, assets.size());

		assetRepository.saveAll(assets);
	}
}
