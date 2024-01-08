package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import java.time.LocalDateTime;
import java.util.List;

public record JustInSequenceV2(String catenaXId, List<SerialPartV2.LocalIdentifier> localIdentifiers,
                               SerialPartV2.PartTypeInformation partTypeInformation,
                               SerialPartV2.ManufacturingInformation manufacturingInformation) {

    public record LocalIdentifier(String value, String key) {
    }

    public record ManufacturingInformation(LocalDateTime date, String country) {
    }

    public record PartTypeInformation(String manufacturerPartId, String classification, String nameAtManufacturer, String customerPartId, String nameAtCustomer) {
    }

}







