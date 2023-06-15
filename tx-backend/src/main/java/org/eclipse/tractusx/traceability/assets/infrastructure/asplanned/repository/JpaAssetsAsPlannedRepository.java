package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository;

import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaAssetsAsPlannedRepository extends JpaRepository<AssetAsPlannedEntity, String> {
    @Query("SELECT asset FROM AssetAsBuiltEntity asset WHERE asset.owner = :owner")
    Page<AssetAsBuiltEntity> findByOwner(Pageable pageable, @Param("owner") Owner owner);

    List<AssetAsBuiltEntity> findByIdIn(List<String> assetIds);

    @Query("SELECT COUNT(asset) FROM AssetAsBuiltEntity asset WHERE asset.owner = :owner")
    long countAssetsByOwner(@Param("owner") Owner owner);
}
