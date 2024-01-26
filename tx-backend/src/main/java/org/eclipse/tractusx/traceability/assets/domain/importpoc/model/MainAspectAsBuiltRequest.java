package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

import java.util.List;

public record MainAspectAsBuiltRequest(List<LocalIdentifier> localIdentifiers,
                                       ManufacturingInformation manufacturingInformation,
                                       String catenaXId,
                                       PartTypeInformation partTypeInformation) {

    public record LocalIdentifier(String value, String key) {
    }

    public record ManufacturingInformation(String date, String country) {
    }

    public record PartTypeInformation(String nameAtCustomer, String customerPartId, String manufacturerPartId, String classification, String nameAtManufacturer) {
    }

}
