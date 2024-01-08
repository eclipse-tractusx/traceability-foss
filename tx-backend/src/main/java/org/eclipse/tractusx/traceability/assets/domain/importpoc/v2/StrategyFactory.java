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
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsBuiltMainAspect;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsPlannedMainAspect;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isMainAspect;

@Component
public class StrategyFactory {

    //use getShape method to get object of type shape
    public AssetBase map(ImportRequestV2.AssetImportRequestV2 importRequestV2) {

        Optional<String> isMainAspectSubmodel = importRequestV2.submodels().stream().filter(genericSubmodel -> isMainAspect(genericSubmodel.getAspectType())).map(GenericSubmodel::getAspectType).findFirst();

        if (isMainAspectSubmodel.isEmpty()) {
            return null;
        }
        if (isAsPlannedMainAspect(isMainAspectSubmodel.get())) {
            return new AsPlannedMainAspectStrategy().map(importRequestV2);
        }
        if (isAsBuiltMainAspect(isMainAspectSubmodel.get())) {
            return new AsBuiltMainAspectStrategy().map(importRequestV2);
        }

        return null;
    }
}

