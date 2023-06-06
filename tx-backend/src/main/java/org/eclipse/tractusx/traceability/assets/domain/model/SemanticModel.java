package org.eclipse.tractusx.traceability.assets.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class SemanticModel {
    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String manufacturerPartId;
    private String customerPartId;
    private String nameAtManufacturer;
    private String nameAtCustomer;
}
