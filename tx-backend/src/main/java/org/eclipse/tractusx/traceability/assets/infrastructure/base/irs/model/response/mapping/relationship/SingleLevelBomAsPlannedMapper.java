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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.relationship;

import org.eclipse.tractusx.irs.component.GlobalAssetIdentification;
import org.eclipse.tractusx.irs.component.Relationship;
import org.eclipse.tractusx.irs.component.enums.Direction;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class SingleLevelBomAsPlannedMapper implements RelationshipMapper {
    private static final String SINGLE_LEVEL_USAGE_AS_BUILT = "SingleLevelUsageAsBuilt";
    private static final String SINGLE_LEVEL_BOM_AS_BUILT = "SingleLevelBomAsBuilt";
    private static final String SINGLE_LEVEL_BOM_AS_PLANNED = "SingleLevelBomAsPlanned";

    @Override
    public AssetBase.AssetBaseBuilder extractRelationship(List<Relationship> relationships) {
        // TODO
        Map<GlobalAssetIdentification, List<Relationship>> relationShipMap = relationships.stream()
                .collect(Collectors.groupingBy(Relationship::getCatenaXId, Collectors.toList()));
        return AssetBase.builder();
    }

    @Override
    public boolean validMapper(Relationship relationship) {
        return relationship.getAspectType().equals("SingleLevelBomAsPlanned");
    }

    @Override
    public Direction direction() {
        return Direction.DOWNWARD;
    }
}
