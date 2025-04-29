package org.eclipse.tractusx.traceability.digitaltwinpart.infrastructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable  // read only
@Table(name = "digital_twin_part_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalTwinPartEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "aas_id")
    private String aasId;

    @Column(name = "aas_ttl")
    private Integer aasTTL;

    @Column(name = "aas_expiration_date")
    private LocalDateTime aasExpirationDate;

    @Column(name = "global_asset_id")
    private String globalAssetId;

    @Column(name = "asset_ttl")
    private Integer assetTTL;

    @Column(name = "asset_expiration_date")
    private LocalDateTime assetExpirationDate;

    @Column(name = "actor")
    private String actor;

    @Column(name = "bpn")
    private String bpn;

    @Column(name = "digital_twin_type")
    private String digitalTwinType;

    public static DigitalTwinPart toDomain(DigitalTwinPartEntity entity) {
        return DigitalTwinPart.builder()
                .aasId(entity.getAasId())
                .aasExpirationDate(entity.getAasExpirationDate())
                .globalAssetId(entity.getGlobalAssetId())
                .assetExpirationDate(entity.getAssetExpirationDate())
                .bpn(BPN.of(entity.getBpn()))
                .digitalTwinType(entity.getDigitalTwinType())
                .build();
    }

    public static DigitalTwinPartDetail toDomainDetail(DigitalTwinPartEntity entity) {
        return DigitalTwinPartDetail.builder()
                .aasId(entity.getAasId())
                .aasTTL(entity.getAasTTL())
                .aasExpirationDate(entity.getAasExpirationDate())
                .globalAssetId(entity.getGlobalAssetId())
                .assetTTL(entity.getAssetTTL())
                .assetExpirationDate(entity.getAssetExpirationDate())
                .actor(entity.getActor())
                .bpn(BPN.of(entity.getBpn()))
                .digitalTwinType(entity.getDigitalTwinType())
                .build();
    }

}
