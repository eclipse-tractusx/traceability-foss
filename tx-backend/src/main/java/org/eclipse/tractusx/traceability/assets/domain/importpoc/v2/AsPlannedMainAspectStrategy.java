/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.v2;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequestV2;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;

import java.util.List;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsPlannedMainAspect;

public class AsPlannedMainAspectStrategy implements MappingStrategy {
    @Override
    public AssetBase map(ImportRequestV2.AssetImportRequestV2 assetImportRequestV2) {

        GenericSubmodel mainAspectSubmodel = assetImportRequestV2.submodels().stream().filter(genericSubmodel -> isAsPlannedMainAspect(genericSubmodel.getAspectType())).findFirst().get();

        List<GenericSubmodel> otherAspectTypes = assetImportRequestV2.submodels().stream().filter(genericSubmodel -> !(genericSubmodel.getPayload() instanceof AsBuiltMainAspectV2)).toList();

        return AssetBase.builder().build();
    }


}
