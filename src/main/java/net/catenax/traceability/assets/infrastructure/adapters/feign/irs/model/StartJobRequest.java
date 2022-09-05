package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import java.util.Arrays;
import java.util.List;

public record StartJobRequest(
	List<String> aspects,
	String globalAssetId,
	boolean collectAspects
) {
	public static StartJobRequest forGlobalAssetId(String globalAssetId) {
		return new StartJobRequest(Aspect.allAspects(), globalAssetId, true);
	}
}

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
