package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BomLifecycle {
    @JsonProperty("asBuilt")
    AS_BUILT,
    @JsonProperty("asPlanned")
    AS_PLANNED
}
