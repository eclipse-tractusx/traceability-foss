package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

import java.util.List;

public record MainAspectAsPlannedRequest(String catenaXId, PartTypeInformation partTypeInformation, List<PartSitesInformationAsPlanned> partSitesInformationAsPlanned
) {

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer) {
    }

    public record PartSitesInformationAsPlanned(String catenaXsiteId, String function, String functionValidFrom, String functionValidUntil) {
    }

}
