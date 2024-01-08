package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import java.time.LocalDateTime;
import java.util.List;

public record SerialPartV2(String catenaXId, List<LocalIdentifier> localIdentifiers,
                           PartTypeInformation partTypeInformation,
                           ManufacturingInformation manufacturingInformation) {

    public record LocalIdentifier(String value, String key) {
    }

    public record ManufacturingInformation(LocalDateTime date, String country) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer, String customerPartId, String nameAtCustomer) {
    }

}






