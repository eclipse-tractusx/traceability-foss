package org.eclipse.tractusx.traceability.assets.domain.model;

import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.traceability.assets.infrastructure.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.springframework.util.StringUtils;

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

    public static SemanticModel from(AssetBaseEntity assetBaseEntity) {
        return SemanticModel.builder()
                .customerPartId(assetBaseEntity.getCustomerPartId())
                .manufacturerPartId(assetBaseEntity.getManufacturerPartId())
                .manufacturingCountry(assetBaseEntity.getManufacturingCountry())
                .manufacturingDate(assetBaseEntity.getManufacturingDate())
                .nameAtCustomer(assetBaseEntity.getNameAtCustomer())
                .nameAtManufacturer(assetBaseEntity.getNameAtManufacturer())
                .build();
    }

    public static SemanticModel from(ShellDescriptor shellDescriptor) {
        return SemanticModel.builder()
                .manufacturerPartId(shellDescriptor.getManufacturerPartId())
                .nameAtManufacturer(shellDescriptor.getIdShort())
                .nameAtCustomer(shellDescriptor.getIdShort())
                .manufacturingCountry("--")
                .manufacturerPartId(defaultValue(shellDescriptor.getManufacturerPartId()))
                .build();
    }

    public static SemanticModel from(PartTypeInformation partTypeInformation) {
        return SemanticModel.builder()
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .nameAtManufacturer(defaultValue(partTypeInformation.nameAtManufacturer()))
                .nameAtCustomer(defaultValue(partTypeInformation.nameAtCustomer()))
                .manufacturingCountry("--")
                .manufacturerPartId(defaultValue(partTypeInformation.manufacturerPartId()))
                .build();
    }

    private static String defaultValue(String value) {
        final String EMPTY_TEXT = "--";
        if (!StringUtils.hasText(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

}
