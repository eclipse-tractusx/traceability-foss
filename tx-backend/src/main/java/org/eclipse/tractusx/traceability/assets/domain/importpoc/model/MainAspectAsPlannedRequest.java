package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

public record MainAspectAsPlannedRequest(String catenaXId, ValidityPeriod validityPeriod,
                                         PartTypeInformation partTypeInformation
) {

    public record ValidityPeriod(
            String validFrom,
            String validTo) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }


}
