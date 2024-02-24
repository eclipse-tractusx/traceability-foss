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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.AssetBaseMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.BatchMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.SerialPartMapper;
import org.eclipse.tractusx.traceability.generated.Batch200Schema;
import org.eclipse.tractusx.traceability.generated.SerialPart101Schema;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class MapperFactory {
    public List<AssetBase> mapToAssetBaseList(IRSResponse irsResponse) {

        return irsResponse
                .submodels()
                .stream()
                .map(irsSubmodel -> getMapper(irsSubmodel).toAssetBase(irsSubmodel))
                .toList();
    }

    private AssetBaseMapper getMapper(IrsSubmodel irsSubmodel) {
        if (irsSubmodel.getPayload() instanceof SerialPart101Schema) {
            return new SerialPartMapper();
        }
        if (irsSubmodel.getPayload() instanceof Batch200Schema) {
            return new BatchMapper();
        }
        throw new IllegalArgumentException("No matching mapper available.");
    }

    protected abstract AssetBaseMapper createMapper();
}
