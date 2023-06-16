package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.SemanticModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_planned")
public class AssetAsPlannedEntity extends AssetBaseEntity {

    @ElementCollection
    @CollectionTable(name = "assets_as_planned_childs", joinColumns = {@JoinColumn(name = "asset_as_planned_id")})
    private List<AssetAsPlannedEntity.ChildDescription> childDescriptors;

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<AlertNotificationEntity> alertNotificationEntities = new ArrayList<>();

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ChildDescription {
        private String id;
        private String idShort;
    }

    public static AssetAsPlannedEntity from(Asset asset) {
        return AssetAsPlannedEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .nameAtManufacturer(asset.getSemanticModel().getNameAtManufacturer())
                .manufacturerPartId(asset.getSemanticModel().getManufacturerPartId())
                .nameAtCustomer(asset.getSemanticModel().getNameAtCustomer())
                .customerPartId(asset.getSemanticModel().getCustomerPartId())
                .owner(asset.getOwner())
                .classification(asset.getClassification())
                .childDescriptors(asset.getChildRelations().stream()
                        .map(child -> new AssetAsPlannedEntity.ChildDescription(child.id(), child.idShort()))
                        .toList())
                .qualityType(asset.getQualityType())
                .activeAlert(asset.isActiveAlert())
                .inInvestigation(asset.isUnderInvestigation())
                .semanticDataModel(SemanticDataModelEntity.from(asset.getSemanticDataModel()))
                .build();
    }

    public static Asset toDomain(AssetAsPlannedEntity entity) {
        return Asset.builder()
                .id(entity.getId())
                .classification(entity.getClassification())
                .idShort(entity.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(entity.getSemanticDataModel()))
                .semanticModel(SemanticModel.from(entity))
                .owner(entity.getOwner())
                .childRelations(entity.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort()))
                        .toList())
                .underInvestigation(entity.isInInvestigation())
                .activeAlert(entity.isActiveAlert())
                .qualityType(entity.getQualityType())
                .build();
    }

    public static List<Asset> toDomainList(List<AssetAsPlannedEntity> entities) {
        return entities.stream()
                .map(AssetAsPlannedEntity::toDomain)
                .toList();
    }

    public static List<AssetAsPlannedEntity> fromList(List<Asset> assets) {
        return assets.stream()
                .map(AssetAsPlannedEntity::from)
                .toList();
    }
}
