package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;

import java.util.List;

public interface AssetBaseMappers<T> {
    List<AssetBase> toAssetBaseList(T input);
}
