package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

import java.time.LocalDateTime;

public record MainAspectAsPlannedRequest(String catenaXId, ValidityPeriod validityPeriod,
                                         PartTypeInformation partTypeInformation
) {

    public record ValidityPeriod(LocalDateTime validFrom, LocalDateTime validTo) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }


}
