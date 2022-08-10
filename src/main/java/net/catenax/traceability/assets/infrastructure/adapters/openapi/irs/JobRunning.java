package net.catenax.traceability.assets.infrastructure.adapters.openapi.irs;

import net.catenax.traceability.assets.infrastructure.adapters.openapi.irs.IRSApiClient.JobResponse;

import java.util.function.Predicate;

public class JobRunning implements Predicate<JobResponse> {
	@Override
	public boolean test(JobResponse jobResponse) {
		return jobResponse.isJobRunning();
	}
}
