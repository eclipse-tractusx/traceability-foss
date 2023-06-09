package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.domain.model.SemanticModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AssetTestDataFactory {


    public static Asset createAssetTestDataWithRelations(List<Descriptions> parents, List<Descriptions> childs) {
        Asset assetTestData = createAssetTestData();
        assetTestData.setParentRelations(parents);
        assetTestData.setChildRelations(childs);
        return assetTestData;
    }

    public static Asset createAssetTestData() {
        Instant manufacturingDate = Instant.now();
        SemanticModel semanticModel =
                SemanticModel.builder()
                        .customerPartId("customerPartId")
                        .manufacturerPartId("customer123")
                        .manufacturingCountry("manu456")
                        .manufacturingDate(manufacturingDate)
                        .nameAtManufacturer("Manufacturer Name")
                        .nameAtCustomer("Customer Name")
                        .build();

        return Asset.builder()
                .id("1")
                .idShort("1234")
                .semanticModelId("456")
                .semanticModel(semanticModel)
                .semanticDataModel(SemanticDataModel.SERIALPARTTYPIZATION)
                .activeAlert(false)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("van123")
                .qualityType(QualityType.CRITICAL)
                .underInvestigation(false)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("manuId")
                .build();
    }

    public static Asset createAssetParentTestData() {
        Instant manufacturingDate = Instant.now();
        SemanticModel semanticModel =
                SemanticModel.builder()
                        .customerPartId("customerPartParentId")
                        .manufacturerPartId("customer123")
                        .manufacturingCountry("manu456")
                        .manufacturingDate(manufacturingDate)
                        .nameAtManufacturer("Manufacturer Name")
                        .nameAtCustomer("Customer Name")
                        .build();

        return Asset.builder()
                .id("2")
                .idShort("23456")
                .semanticModelId("456")
                .semanticModel(semanticModel)
                .semanticDataModel(SemanticDataModel.SERIALPARTTYPIZATION)
                .activeAlert(false)
                .parentRelations(AssetTestDataFactory.provideParentRelations())
                .childRelations(AssetTestDataFactory.provideChildRelations())
                .van("van123")
                .qualityType(QualityType.CRITICAL)
                .underInvestigation(false)
                .owner(Owner.OWN)
                .manufacturerName("manuName")
                .manufacturerId("manuId")
                .build();
    }

    public static List<Descriptions> provideParentRelations() {
        List<Descriptions> parentDescriptions = new ArrayList<>();
        parentDescriptions.add(new Descriptions("parent1", "desc1"));
        parentDescriptions.add(new Descriptions("parent2", "desc2"));
        return parentDescriptions;
    }

    public static List<Descriptions> provideChildRelations() {
        List<Descriptions> childDescriptions = new ArrayList<>();
        childDescriptions.add(new Descriptions("child1", "desc1"));
        childDescriptions.add(new Descriptions("child2", "desc2"));
        return childDescriptions;
    }
}
