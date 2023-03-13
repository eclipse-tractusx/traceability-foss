package org.eclipse.tractusx.traceability.investigations.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Describes the criticality of a notification")
public enum Severity {
	MINOR,
	MAJOR,
	CRITICAL,
	@ApiModelProperty(name = "LIFE-THREATENING")
	LIFE_THREATENING
}
