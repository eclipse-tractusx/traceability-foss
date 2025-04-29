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

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.irs.component.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asbuilt.AsBuiltDetailMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asplanned.AsPlannedDetailMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.relationship.RelationshipMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.SubmodelMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.relationship.SubmodelRelationshipMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssetBaseMapperProvider {
    private final List<SubmodelMapper> baseMappers;
    private final List<SubmodelRelationshipMapper> submodelRelationshipMappers;
    private final List<RelationshipMapper> relationshipMappers;
    private final List<AsPlannedDetailMapper> asPlannedDetailMappers;
    private final List<AsBuiltDetailMapper> asBuiltDetailMappers;

    public Optional<SubmodelMapper> getMainSubmodelMapper(IrsSubmodel irsSubmodel) {
        return baseMappers.stream().filter(assetBaseMapper -> assetBaseMapper.validMapper(irsSubmodel)).findFirst();
    }

    public Optional<SubmodelRelationshipMapper> getRelationshipSubmodelMapper(IrsSubmodel irsSubmodel) {
        return submodelRelationshipMappers.stream().filter(submodelRelationshipMapper -> submodelRelationshipMapper.validMapper(irsSubmodel)).findFirst();
    }

    public Optional<RelationshipMapper> getRelationshipMapper(Relationship relationship) {
        return relationshipMappers.stream().filter(relationshipMapper -> relationshipMapper.validMapper(relationship)).findFirst();
    }

    public Optional<AsPlannedDetailMapper> getAsPlannedDetailMapper(IrsSubmodel irsSubmodel) {
        return asPlannedDetailMappers.stream().filter(asPlannedDetailMapper -> asPlannedDetailMapper.validMapper(irsSubmodel)).findFirst();
    }

    public Optional<AsBuiltDetailMapper> getAsBuiltDetailMapper(IrsSubmodel irsSubmodel) {
        return asBuiltDetailMappers.stream().filter(asBuiltDetailMapper -> asBuiltDetailMapper.validMapper(irsSubmodel)).findFirst();
    }
}
