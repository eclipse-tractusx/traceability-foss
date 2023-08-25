package org.eclipse.tractusx.traceability.assets.application.base.service;

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;

import java.util.List;
import java.util.Map;

public interface AssetBaseService {
    void synchronizeAssetsAsync(List<String> globalAssetIds);

    void synchronizeAssetsAsync(String globalAssetId);

    void setAssetsInvestigationStatus(QualityNotification investigation);

    void setAssetsAlertStatus(QualityNotification alert);

    Map<String, Long> getAssetsCountryMap();

}
