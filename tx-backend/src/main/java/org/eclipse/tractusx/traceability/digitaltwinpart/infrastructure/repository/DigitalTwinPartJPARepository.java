package org.eclipse.tractusx.traceability.digitaltwinpart.infrastructure.repository;

import org.eclipse.tractusx.traceability.digitaltwinpart.infrastructure.model.DigitalTwinPartEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DigitalTwinPartJPARepository extends JpaRepository<DigitalTwinPartEntity, String>, JpaSpecificationExecutor<DigitalTwinPartEntity> {

    @Query("SELECT d FROM DigitalTwinPartEntity d WHERE d.aasId = :id OR d.globalAssetId = :id")
    Optional<DigitalTwinPartEntity> findByAasIdOrGlobalAssetId(@Param("id") @NotNull String id);
}
