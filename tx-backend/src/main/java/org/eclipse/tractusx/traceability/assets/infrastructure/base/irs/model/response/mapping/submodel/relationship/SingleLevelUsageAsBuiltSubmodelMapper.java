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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.relationship;

import org.eclipse.tractusx.irs.component.enums.Direction;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.generated.SingleLevelBomAsBuilt300Schema;
import org.eclipse.tractusx.traceability.generated.SingleLevelUsageAsBuilt300Schema;
import org.eclipse.tractusx.traceability.generated.UrnSammIoCatenaxSingleLevelUsageAsBuilt300ParentData;
import org.springframework.stereotype.Component;


@Component
public class SingleLevelUsageAsBuiltSubmodelMapper implements SubmodelRelationshipMapper {

    @Override
    public Descriptions extractDescription(IrsSubmodel irsSubmodel) {
        SingleLevelUsageAsBuilt300Schema singleLevelBomAsBuilt = (SingleLevelUsageAsBuilt300Schema) irsSubmodel.getPayload();
        String childCatenaXId = singleLevelBomAsBuilt.getCatenaXId();

        String parentItems = singleLevelBomAsBuilt.getParentItems()
                .stream()
                .findFirst()
                .map(UrnSammIoCatenaxSingleLevelUsageAsBuilt300ParentData::getCatenaXId)
                .orElse(null);

        return new Descriptions(parentItems, null, childCatenaXId, Direction.UPWARD);
    }

    @Override
    public boolean validMapper(IrsSubmodel irsSubmodel) {
        return irsSubmodel.getPayload() instanceof SingleLevelUsageAsBuilt300Schema;
    }

}
