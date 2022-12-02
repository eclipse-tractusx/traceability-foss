package net.catenax.traceability.infrastructure.edc.blackbox.model;

import net.catenax.traceability.infrastructure.edc.blackbox.Constants;
import net.catenax.traceability.investigations.domain.model.InvestigationStatus;

import javax.validation.constraints.NotNull;

public record UpdateQualityInvestigationStatus(
	@NotNull(message = Constants.STATUS_MUST_BE_PRESENT) InvestigationStatus status) {
}
