package org.eclipse.tractusx.traceability.assets.application.base.service;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AssetBaseService {
    void synchronizeAssetsAsync(List<String> globalAssetIds);

    void synchronizeAssetsAsync(String globalAssetId);

    void setAssetsInvestigationStatus(QualityNotification investigation);

    void setAssetsAlertStatus(QualityNotification alert);

    AssetBase updateQualityType(String assetId, QualityType qualityType);

    Map<String, Long> getAssetsCountryMap();

    PageResult<AssetBase> getAssets(Pageable pageable, Owner owner);

    AssetBase getAssetById(String assetId);

    List<AssetBase> getAssetsById(List<String> assetIds);

    AssetBase getAssetByChildId(String assetId, String childId);
}
