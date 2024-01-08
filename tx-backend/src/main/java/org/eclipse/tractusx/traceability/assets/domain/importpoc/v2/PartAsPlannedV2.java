package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import java.time.LocalDateTime;
import java.util.List;

public record PartAsPlannedV2(String catenaXId, ValidityPeriod validityPeriod,
                              PartTypeInformation partTypeInformation
) {

    public record ValidityPeriod(LocalDateTime validFrom, LocalDateTime validTo) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }


}
