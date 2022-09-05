package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record JobResponse(
	JobStatus jobStatus,
	List<Shell> shells,
	List<SerialPartTypization> serialPartTypizations,
	Map<String, AssemblyPartRelationship> assemblyPartRelationships,
	Map<String, String> bpns
) {

	@JsonCreator
	static JobResponse of(
		@JsonProperty("job") JobStatus jobStatus,
		@JsonProperty("shells") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Shell> shells,
		@JsonProperty("submodels") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Submodel> submodels,
		@JsonProperty("bpns") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Bpn> bpns
	) {
		Map<String, String> bpnsMap = bpns.stream()
			.collect(Collectors.toMap(Bpn::manufacturerId, Bpn::manufacturerName));
		List<SerialPartTypization> serialPartTypizations = submodels.stream()
			.map(Submodel::getPayload)
			.filter(SerialPartTypization.class::isInstance)
			.map(SerialPartTypization.class::cast)
			.toList();
		Map<String, AssemblyPartRelationship> assemblyPartRelationships = submodels.stream()
			.map(Submodel::getPayload)
			.filter(AssemblyPartRelationship.class::isInstance)
			.map(AssemblyPartRelationship.class::cast)
			.collect(Collectors.toMap(AssemblyPartRelationship::catenaXId, Function.identity()));

		return new JobResponse(jobStatus, shells, serialPartTypizations, assemblyPartRelationships, bpnsMap);
	}

	public boolean isRunning() {
		return "RUNNING".equals(jobStatus.jobState());
	}

	public boolean isCompleted() {
		return "COMPLETED".equals(jobStatus.jobState());
	}

}

record JobStatus(
	String jobState
) {}

record Bpn(
	String manufacturerId,
	String manufacturerName
) {}
