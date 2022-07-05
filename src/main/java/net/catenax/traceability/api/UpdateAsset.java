package net.catenax.traceability.api;

import net.catenax.traceability.assets.QualityType;

import javax.validation.constraints.NotNull;

public record UpdateAsset(
	@NotNull(message = "qualityType must be present") QualityType qualityType) {
}
