package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;

public interface MappingStrategy {
    AssetBase mapToAssetBase(ImportRequest.AssetImportRequest assetImportRequestV2, TraceabilityProperties traceabilityProperties);
}
