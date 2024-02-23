package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.eclipse.tractusx.irs.component.Tombstone;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;

import java.util.List;

public record IRSResponse(@JsonProperty("job") JobStatus jobStatus,
                          @JsonProperty("relationships") List<Relationship> relationships,
                          @JsonProperty("shells") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Shell> shells,
                          @JsonProperty("submodels") @JsonSetter(nulls = Nulls.AS_EMPTY) List<IrsSubmodel> submodels,
                          @JsonProperty("bpns") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Bpn> bpns,
                          @JsonProperty("tombstones") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Tombstone> tombstones) {
}
