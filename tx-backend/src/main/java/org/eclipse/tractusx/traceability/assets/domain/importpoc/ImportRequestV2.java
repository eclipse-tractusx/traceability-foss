package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.AssetMetaInfoRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;

import java.util.List;

public record ImportRequestV2(@JsonProperty("assets") List<AssetImportRequestV2> assets) {
    public record AssetImportRequestV2(@JsonProperty("assetMetaInfo") AssetMetaInfoRequest assetMetaInfoRequest,
                                       @JsonProperty("submodels") List<GenericSubmodel> submodels) {
    }


}
