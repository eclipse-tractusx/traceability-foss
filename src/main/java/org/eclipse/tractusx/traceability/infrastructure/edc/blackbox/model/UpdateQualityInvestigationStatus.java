package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.Constants;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;

import javax.validation.constraints.NotNull;

public record UpdateQualityInvestigationStatus(
	@NotNull(message = Constants.STATUS_MUST_BE_PRESENT) InvestigationStatus status) {
}
