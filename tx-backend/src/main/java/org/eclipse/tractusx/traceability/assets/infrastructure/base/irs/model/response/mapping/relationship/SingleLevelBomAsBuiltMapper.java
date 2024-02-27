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
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.springframework.stereotype.Component;


@Component
public class SingleLevelBomAsBuiltMapper implements RelationshipMapper {
    private static final String SINGLE_LEVEL_BOM_AS_BUILT = "SingleLevelBomAsBuilt";

    @Override
    public Descriptions extractDescription(Relationship relationship) {
        GlobalAssetIdentification childCatenaXId = relationship.getLinkedItem().getChildCatenaXId();
        String catenaXIdString = relationship.getCatenaXId().toString();
        return new Descriptions(childCatenaXId.getGlobalAssetId(), null, catenaXIdString, Direction.DOWNWARD);

    }

    @Override
    public boolean validMapper(Relationship relationship) {
        return relationship.getAspectType().equals(SINGLE_LEVEL_BOM_AS_BUILT);
    }

}
