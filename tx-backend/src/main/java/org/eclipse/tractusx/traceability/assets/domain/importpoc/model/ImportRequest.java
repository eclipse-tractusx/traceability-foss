package org.eclipse.tractusx.traceability.assets.domain.importpoc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;

import java.util.List;

public record ImportRequest(@JsonProperty("assets") List<AssetImportRequest> assets) {
    public record AssetImportRequest(@JsonProperty("assetMetaInfo") AssetMetaInfoRequest assetMetaInfoRequest,
                                     @JsonProperty("submodels") List<IrsSubmodel> submodels) {
    }


}
