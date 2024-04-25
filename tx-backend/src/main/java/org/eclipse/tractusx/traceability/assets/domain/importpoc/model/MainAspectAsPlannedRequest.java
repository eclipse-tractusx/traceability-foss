package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

public record MainAspectAsPlannedRequest(String catenaXId, PartTypeInformation partTypeInformation, PartSitesInformationAsPlanned partSitesInformationAsPlanned
) {

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }

    public record PartSitesInformationAsPlanned(String catenaXsiteId, String function, String functionValidFrom, String functionValidTo) {
    }


}
