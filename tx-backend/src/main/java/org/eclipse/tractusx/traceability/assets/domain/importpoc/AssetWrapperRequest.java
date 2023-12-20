/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Submodel;

import java.util.Collections;
import java.util.List;

public record AssetWrapperRequest(AssetMetaInfoRequest assetMetaInfoRequest,
                                  List<Submodel> mainAspectModels,
                                  List<Submodel> upwardRelationship,
                                  List<Submodel> downwardRelationship
) {


    @JsonCreator
    static AssetWrapperRequest of(
            @JsonProperty("assetMetaInfo") AssetMetaInfoRequest assetMetaInfoRequest,
            @JsonProperty("submodels") List<Submodel> submodels
    ) {
        List<Submodel> upwardSubmodels = submodels.stream().filter(submodel -> isUpwardRelationship(submodel.getAspectType())).toList();
        List<Submodel> downwardSubmodels = submodels.stream().filter(submodel -> isDownwardRelationship(submodel.getAspectType())).toList();
        List<Submodel> mainAspectSubmodels = submodels.stream().filter(submodel -> isMainAspect(submodel.getAspectType())).toList();
        return new AssetWrapperRequest(assetMetaInfoRequest, mainAspectSubmodels, upwardSubmodels, downwardSubmodels);

    }

    public List<AssetBase> convertAssets() {
        return Collections.emptyList();
    }

    private static boolean isUpwardRelationship(final String aspectType) {
        return aspectType.contains("BOM");
    }

    private static boolean isDownwardRelationship(final String aspectType) {
        return aspectType.contains("Usage");
    }

    private static boolean isMainAspect(final String aspectType) {
        return !isDownwardRelationship(aspectType) && !isUpwardRelationship(aspectType);
    }
}
