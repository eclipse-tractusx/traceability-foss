package net.catenax.traceability.assets.infrastructure.adapters.rest.assets;

import net.catenax.traceability.assets.domain.QualityType;

import javax.validation.constraints.NotNull;

public record UpdateAsset(
	@NotNull(message = "qualityType must be present") QualityType qualityType) {
}
