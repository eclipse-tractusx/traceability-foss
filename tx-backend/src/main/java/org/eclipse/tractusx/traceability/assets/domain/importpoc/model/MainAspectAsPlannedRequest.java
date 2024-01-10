package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record MainAspectAsPlannedRequest(String catenaXId, ValidityPeriod validityPeriod,
                                         PartTypeInformation partTypeInformation
) {

    public record ValidityPeriod(OffsetDateTime validFrom, OffsetDateTime validTo) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }


}
