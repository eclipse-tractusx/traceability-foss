package net.catenax.traceability.assets.infrastructure.adapters.openapi.irs;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Param;
import feign.RequestLine;
import io.github.resilience4j.retry.annotation.Retry;
import net.catenax.traceability.assets.infrastructure.config.openapi.CatenaApiConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;

@FeignClient(
	name = "irsApi",
	url = "${feign.irsApi.url}",
	configuration = {CatenaApiConfig.class}
)
public interface IRSApiClient {

	@RequestLine("POST /irs/jobs")
	StartJobResponse registerJob(@RequestBody StartJobRequest request);

	@RequestLine("GET /irs/jobs/{jobId}")
	@Retry(name = "irs-get")
	JobResponse getJobDetails(@Param("jobId") String jobId);

	enum Aspect {
		SERIAL_PART_TYPIZATION("SerialPartTypization"),
		ASSEMBLY_PART_TYPIZATION("AssemblyPartRelationship");

		private final String aspectName;

		Aspect(String aspectName) {
			this.aspectName = aspectName;
		}

		public String getAspectName() {
			return aspectName;
		}

		public static List<String> allAspects() {
			return Arrays.stream(Aspect.values())
				.map(Aspect::getAspectName)
				.toList();
		}
	}

	record JobResponse(
		@JsonProperty("job") JobStatus jobStatus,
		List<Submodel> submodels,
		List<Shell> shells
	) {
		boolean isJobRunning() {
			return "RUNNING".equals(jobStatus.jobState);
		}
	}

	record JobStatus(
		String jobState
	) {}

	record Shell(
		String idShort,
		String identification
	) {}

	record Submodel(
		String identification,
		String aspectType,
		String payload
	) {}

	record StartJobResponse(
		String jobId
	) {}

	record StartJobRequest(
		List<String> aspects,
		String globalAssetId,
		boolean collectAspects
	) {
		public static StartJobRequest forGlobalAssetId(String globalAssetId) {
			return new StartJobRequest(Aspect.allAspects(), globalAssetId, true);
		}
	}
}
