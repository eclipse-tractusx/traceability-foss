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

package org.eclipse.tractusx.traceability.assets.domain.asbuilt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.service.AbstractAssetBaseService;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetAsBuiltServiceImpl extends AbstractAssetBaseService {

    private final AssetAsBuiltRepository assetAsBuiltRepository;

    private final IrsRepository irsRepository;

    @Override
    protected AssetRepository getAssetRepository() {
        return assetAsBuiltRepository;
    }

    @Override
    protected List<String> getDownwardAspects() {
        return Aspect.downwardAspectsForAssetsAsBuilt();
    }

    @Override
    protected List<String> getUpwardAspects() {
        return Aspect.upwardAspectsForAssetsAsBuilt();
    }

    @Override
    protected BomLifecycle getBomLifecycle() {
        return BomLifecycle.AS_BUILT;
    }

    @Override
    protected IrsRepository getIrsRepository() {
        return irsRepository;
    }


}
