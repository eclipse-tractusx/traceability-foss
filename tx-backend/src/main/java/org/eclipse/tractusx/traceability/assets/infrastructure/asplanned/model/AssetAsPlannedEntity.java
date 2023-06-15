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
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
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
    @CollectionTable(name = "assets_as_planned_childs", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsPlannedEntity.ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "assets_as_planned_parents", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsPlannedEntity.ParentDescription> parentDescriptors;

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

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ParentDescription {
        private String id;
        private String idShort;
    }
}
