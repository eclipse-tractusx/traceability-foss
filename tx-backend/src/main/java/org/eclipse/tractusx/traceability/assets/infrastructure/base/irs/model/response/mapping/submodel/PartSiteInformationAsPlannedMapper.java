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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.generated.PartSiteInformationAsPlanned100Schema;
import org.springframework.stereotype.Component;


@Component
public class PartSiteInformationAsPlannedMapper implements SubmodelMapper {
    @Override
    public AssetBase.AssetBaseBuilder extractSubmodel(IrsSubmodel irsSubmodel) {
        PartSiteInformationAsPlanned100Schema partSiteInformationAsPlanned = (PartSiteInformationAsPlanned100Schema) irsSubmodel.getPayload();
        return AssetBase
                .builder()
                .id(partSiteInformationAsPlanned.getCatenaXId());
    }

    @Override
    public boolean validMapper(IrsSubmodel submodel) {
        return submodel.getPayload() instanceof PartSiteInformationAsPlanned100Schema;
    }
}
