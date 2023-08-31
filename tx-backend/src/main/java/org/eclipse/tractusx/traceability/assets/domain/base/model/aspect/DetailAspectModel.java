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
package org.eclipse.tractusx.traceability.assets.domain.base.model.aspect;

import assets.response.asbuilt.DetailAspectDataAsBuiltResponse;
import assets.response.asplanned.DetailAspectDataAsPlannedResponse;
import assets.response.asplanned.PartSiteInformationAsPlannedResponse;
import assets.response.base.DetailAspectDataResponse;
import assets.response.base.DetailAspectModelResponse;
import assets.response.base.DetailAspectTypeResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;

import java.util.List;

@Getter
@Setter
@Builder
public class DetailAspectModel {
    private DetailAspectType type;
    private DetailAspectData data;

    public static DetailAspectModel from(DetailAspectModelResponse detailAspectModelResponse) {
        DetailAspectTypeResponse detailAspectType = detailAspectModelResponse.getType();
        DetailAspectDataResponse detailAspectData = detailAspectModelResponse.getData();
        return DetailAspectModel.builder()
                .type(DetailAspectType.valueOf(detailAspectType.name()))
                .data(from(detailAspectData)).build();

    }

    public static DetailAspectData from(DetailAspectDataResponse detailAspectDataResponse) {
        if (detailAspectDataResponse instanceof DetailAspectDataAsBuiltResponse) {
            return DetailAspectDataAsBuilt.builder()
                    .partId(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getPartId())
                    .customerPartId(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getCustomerPartId())
                    .nameAtCustomer(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getNameAtCustomer())
                    .manufacturingCountry(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getManufacturingCountry())
                    .manufacturingDate(((DetailAspectDataAsBuiltResponse) detailAspectDataResponse).getManufacturingDate())
                    .build();
        }

        if (detailAspectDataResponse instanceof DetailAspectDataAsPlannedResponse) {
            return DetailAspectDataAsPlanned.builder()
                    .validityPeriodTo(((DetailAspectDataAsPlannedResponse) detailAspectDataResponse).getValidityPeriodTo())
                    .validityPeriodFrom(((DetailAspectDataAsPlannedResponse) detailAspectDataResponse).getValidityPeriodFrom())
                    .build();
        }

        if (detailAspectDataResponse instanceof PartSiteInformationAsPlannedResponse) {
            return DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getCatenaXSiteId())
                    .functionValidFrom(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunctionValidFrom())
                    .function(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunction())
                    .functionValidUntil(((PartSiteInformationAsPlannedResponse) detailAspectDataResponse).getFunctionValidUntil())
                    .build();
        }
        return DetailAspectDataPartSiteInformationAsPlanned.builder().build();
    }


    public static List<DetailAspectModel> from(AssetAsBuiltEntity entity) {
        // todo map all from entity to list of detail aspect model. Then do the same for assetAsPlanned

        DetailAspectModel detailAspectModel = DetailAspectModel.builder()
                .type(DetailAspectType.AS_BUILT)
                .data(DetailAspectDataAsBuilt.builder()
                        .partId(entity.getManufacturerPartId())
                        .customerPartId(entity.getCustomerPartId())
                        .nameAtCustomer(entity.getNameAtCustomer())
                        .manufacturingCountry(entity.getManufacturingCountry())
                        .manufacturingDate(entity.getManufacturingDate().toString())
                        .build())
                .build();

        return List.of(detailAspectModel);
    }

    public static List<DetailAspectModel> from(AssetAsPlannedEntity entity) {
        // todo map all from entity to list of detail aspect model. Then do the same for assetAsPlanned

        DetailAspectModel detailAspectModel = DetailAspectModel.builder()
                .type(DetailAspectType.AS_PLANNED)
                .data(DetailAspectDataAsPlanned.builder()
                        .validityPeriodFrom(entity.get)
                        .validityPeriodTo()
                        .build())
                .build();

        return List.of(detailAspectModel);
    }
}
