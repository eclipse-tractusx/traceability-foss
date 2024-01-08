package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequestV2;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;

public interface MappingStrategy {
    AssetBase mapToAssetBase(ImportRequestV2.AssetImportRequestV2 assetImportRequestV2, TraceabilityProperties traceabilityProperties);
}
