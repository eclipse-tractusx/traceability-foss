package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequestV2;

public interface MappingStrategy {
    AssetBase map(ImportRequestV2.AssetImportRequestV2 assetImportRequestV2);
}
